package io.github.mooeypoo.playingwithtime.configs.interfaces;

import java.util.Set;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.annote.ConfDefault.DefaultString;
import space.arim.dazzleconf.annote.ConfDefault.DefaultStrings;

public interface DefinitionConfigInterface {
	@ConfKey("time")
	@ConfComments({
		"Time played in-game, in minutes. When the player's time in-game is equal-to or higher-than",
		"this number, the rest of the operation dictated in this definition will be",
		"further evaluated."
	})
	@DefaultString("0")
	String time();
	
	@ConfKey("custom_commands")
	@ConfComments({
		"Custom command that will run if the user matches time played and prerequisites",
		"But will **not** run if the user already has the group/perms under \"add\" groups",
		"Watch out; if nothing is provided under 'add' group above, this command will run",
		"every time the user logs in if their time played is above the value.",
		"Available placeholders for commands:",
		"- %playername% Player name",
		"- %timeingame% Player's time in game, in minutes."
	})
	@DefaultStrings({})
	Set<String> custom_commands();

	@SubSection
	@ConfComments({
		"A list of rules that the user must have along with time played in order for the",
		"rank to apply. This can include group names, permission names, or neither.",
		"All of those rules must apply or the system will skip the process for the user",
		"even if the time played matches.",
		"This is useful to make different time-based rules for users who belong to different",
		"rules. Leave blank for the system to only consider time played and nothing else."
	})
	SubSectionGroupPermListInterface prerequisites();

	@SubSection
	@ConfComments({
		"Add the user to groups/permissions.",
		"Can be one or multiple in either group or permissions lists.",
		"If none provided, no permission/group action will be done."
	})
	SubSectionGroupPermListInterface add();
}
