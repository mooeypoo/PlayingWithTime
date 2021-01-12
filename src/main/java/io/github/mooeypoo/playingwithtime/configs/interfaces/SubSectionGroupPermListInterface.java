package io.github.mooeypoo.playingwithtime.configs.interfaces;

import java.util.Set;

import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.ConfDefault.DefaultStrings;

public interface SubSectionGroupPermListInterface {
	@ConfKey("groups")
	@DefaultStrings({})
	Set<String> groups();

	@ConfKey("permissions")
	@DefaultStrings({})
	Set<String> permissions();
}
