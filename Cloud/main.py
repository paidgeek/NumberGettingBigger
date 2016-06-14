import webapp2
from google.appengine.ext import ndb
from google.appengine.api import urlfetch
import json
import random
import string
from config import *

class MainPage(webapp2.RequestHandler):
	def get(self):
		self.response.headers["Content-Type"] = "text/plain"
		self.response.write("sup bro")

class Player(ndb.Model):
	number = ndb.FloatProperty(indexed=True)
	session_token = ndb.StringProperty()

base64_alphabet = string.letters[:52] + string.digits

def generate_session_token():
	return str().join(random.choice(base64_alphabet) for _ in xrange(160))

@ndb.transactional
def sign_in_player(user_id):
	player = Player.get_by_id(user_id)

	if player:
		player.session_token = generate_session_token()
	else:
		player = Player(id=user_id, session_token=generate_session_token(), number=0.0)
   
	player.put()
	return player

class SignInHandler(webapp2.RequestHandler):
	def post(self):
		self.response.headers["Content-Type"] = "application/json"
		access_token = self.request.get("access_token")

		if not access_token:
			self.response.status_int = 401
			return

		try:
			graph_response = urlfetch.fetch(FACEBOOK_ACCESS_TOKEN_INFO % (access_token))
			if graph_response.status_code == 200:
				result = json.loads(graph_response.content)
				data = result["data"]
				app_id = data["app_id"]
				user_id = data["user_id"]

				if app_id != FACEBOOK_APP_ID:
					self.response.status_int = 401
				else:
					player = sign_in_player(user_id)
					self.response.write(json.dumps(player.to_dict()))
			else:
				self.response.write("error")
		except urlfetch.InvalidURLError:
			self.response.write("error")
		except urlfetch.DownloadError:
			self.response.write("error")

class NumberHandler(webapp2.RequestHandler):
	def post(self):
		self.response.headers["Content-Type"] = "application/json"
		user_id = self.request.headers["X-USER-ID"]
		session_token = self.request.headers["X-SESSION-TOKEN"]
		number = self.request.get("number")

		if not user_id or not session_token or not number:
			self.response.status_int = 400
			return

		player = Player.get_by_id(user_id)

		if player is None or player.session_token != session_token:
			self.response.status_int = 401
			return
		
		player.number = float(number)
		player.put()

class LeaderboardHandler(webapp2.RequestHandler):
	def post(self):
		self.response.headers["Content-Type"] = "application/json"
		user_id = self.request.headers["X-USER-ID"]
		session_token = self.request.headers["X-SESSION-TOKEN"]
		user_ids = self.request.body.split(",")
		
		if not user_id or not session_token or not user_ids:
			self.response.status_int = 400
			return

		player = Player.get_by_id(user_id)

		if player is None or player.session_token != session_token:
			self.response.status_int = 401
			return
		
		players = ndb.get_multi([ndb.Key(Player, k) for k in user_ids])
		self.response.write(json.dumps([p.number if p and p.number else 0.0 for p in players]))

app = webapp2.WSGIApplication([
    ("/", MainPage),
    ("/sign_in", SignInHandler),
	 ("/numbers", NumberHandler),
	 ("/leaderboard", LeaderboardHandler)
], debug=True)
