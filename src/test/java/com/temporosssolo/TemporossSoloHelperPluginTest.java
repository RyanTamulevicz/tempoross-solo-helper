package com.temporosssolo;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TemporossSoloHelperPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(TemporossSoloHelperPlugin.class);
		RuneLite.main(args);
	}
}
