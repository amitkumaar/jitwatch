/*
 * Copyright (c) 2013, 2014 Chris Newland.
 * Licensed under https://github.com/AdoptOpenJDK/jitwatch/blob/master/LICENSE-BSD
 * Instructions: https://github.com/AdoptOpenJDK/jitwatch/wiki
 */
package com.chrisnewland.jitwatch.histo;

import java.util.List;

import com.chrisnewland.jitwatch.model.IMetaMember;
import com.chrisnewland.jitwatch.model.MetaClass;
import com.chrisnewland.jitwatch.model.MetaPackage;
import com.chrisnewland.jitwatch.model.PackageManager;

public class HistoTreeWalker
{
	// Good case for J8 Streams
	public static Histo buildHistoForAttribute(PackageManager pm, boolean compileAttribute, String attributeName, long resolution)
	{
		Histo histo = new Histo(resolution);

		List<MetaPackage> roots = pm.getRootPackages();

		for (MetaPackage mp : roots)
		{
			walkTree(mp, histo, compileAttribute, attributeName);
		}

		return histo;
	}

	private static void walkTree(MetaPackage mp, Histo histo, boolean isCompileAttribute, String attributeName)
	{
		List<MetaPackage> childPackages = mp.getChildPackages();

		for (MetaPackage childPackage : childPackages)
		{
			walkTree(childPackage, histo, isCompileAttribute, attributeName);
		}

		List<MetaClass> packageClasses = mp.getPackageClasses();

		for (MetaClass mc : packageClasses)
		{
			for (IMetaMember mm : mc.getMetaMembers())
			{
				String attrValue = null;

				if (isCompileAttribute)
				{
					attrValue = mm.getCompiledAttribute(attributeName);
				}
				else
				{
					attrValue = mm.getQueuedAttribute(attributeName);
				}

				if (attrValue != null)
				{
					long val = Long.valueOf(attrValue);

					histo.addValue(val);
				}
			}
		}
	}
}
