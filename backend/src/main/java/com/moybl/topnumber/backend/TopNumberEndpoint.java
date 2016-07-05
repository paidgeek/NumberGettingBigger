package com.moybl.topnumber.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;

@Api(
		name = "topNumber",
		description = "Number getting bigger.",
		version = "v1",
		namespace = @ApiNamespace(
				ownerDomain = "backend.topnumber.moybl.com",
				ownerName = "backend.topnumber.moybl.com",
				packagePath = ""
		),
		clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID},
		audiences = {Constants.ANDROID_AUDIENCE}
)
public abstract class TopNumberEndpoint {
}
