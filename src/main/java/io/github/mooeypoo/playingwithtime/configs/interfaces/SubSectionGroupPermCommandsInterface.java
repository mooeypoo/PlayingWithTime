package io.github.mooeypoo.playingwithtime.configs.interfaces;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.ConfDefault.DefaultString;

public interface SubSectionGroupPermCommandsInterface {
	@ConfKey("add_group")
	@ConfComments({
		"Raw command to add a group to a user. The added groups are defined in each config in the 'add' section."
	})
	@DefaultString("lp user %playername% parent set %typename%")
	String add_group();

	@ConfKey("add_permission")
	@ConfComments({
		"Raw command to add permission to a user. The added permissions are defined in each config in the 'add' section."
	})
	@DefaultString("lp user %playername% permission set %typename% true")
	String add_permission();
}