/**
 * Wowza server software and all components Copyright 2006 - 2015, Wowza Media Systems, LLC, licensed pursuant to the Wowza Media Software End User License Agreement.
 */
package com.wowza.wms.plugin;

import com.wowza.util.StringUtils;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.logging.WMSLogger;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.stream.IMediaStream;
import com.wowza.wms.stream.livepacketizer.ILiveStreamPacketizerControl;

public class PacketizerControl extends ModuleBase
{

	private class PacketizerHandler implements ILiveStreamPacketizerControl
	{

		public boolean isLiveStreamPacketize(String packetizer, IMediaStream stream)
		{
			logger.info(MODULE_NAME + ".isLiveStreamPacketize [" + packetizer + " : " + stream.getName() + " : " + suffixes + "]", stream);
			if (suffixes.equals("*"))
			{
				logger.info(MODULE_NAME + ".isLiveStreamPacketize [" + packetizer + " : " + stream.getName() + " :  suffixes is wildcard. returning " + matchAllow + "]", stream);
				return matchAllow;
			}
			if (StringUtils.isEmpty(suffixes))
			{
				logger.info(MODULE_NAME + ".isLiveStreamPacketize [" + packetizer + " : " + stream.getName() + " : suffixes is empty. returning " + noMatchAllow + "]", stream);
				return noMatchAllow;
			}

			String[] suffixArray = suffixes.split(",");
			for (String suffix : suffixArray)
			{
				if (stream.getName().endsWith(suffix.trim()))
				{
					logger.info(MODULE_NAME + ".isLiveStreamPacketize [" + packetizer + " : " + stream.getName() + " : match found : " + suffix.trim() + " : returning " + matchAllow + "]", stream);
					return matchAllow;
				}
			}
			logger.info(MODULE_NAME + ".isLiveStreamPacketize [" + packetizer + " : " + stream.getName() + " : No match found : returning " + noMatchAllow + "]", stream);
			return noMatchAllow;
		}
	}

	public static final String MODULE_NAME = "ModulePacketizerControl";
	public static final String PROP_NAME_PREFIX = "packetizerControl";
	
	private WMSLogger logger;
	private String suffixes = "*";
	private boolean matchAllow = true;
	private boolean noMatchAllow = false;

	public void onAppStart(IApplicationInstance appInstance)
	{
		logger = WMSLoggerFactory.getLoggerObj(appInstance);
		
		suffixes = appInstance.getProperties().getPropertyStr(PROP_NAME_PREFIX + "Suffixes", suffixes);
		matchAllow = appInstance.getProperties().getPropertyBoolean(PROP_NAME_PREFIX + "MatchAllow", matchAllow);
		noMatchAllow = appInstance.getProperties().getPropertyBoolean(PROP_NAME_PREFIX + "NoMatchAllow", noMatchAllow);

		appInstance.setLiveStreamPacketizerControl(new PacketizerHandler());
	}
}