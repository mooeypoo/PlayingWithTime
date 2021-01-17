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
		"Send a message following the processing of this definition. The message can be",
		"sent to the user specifically, or to the entire chat as a broadcast.",
		"Available placeholders for messages:",
		"- %playername% Player name",
		"- %timeingame% Player's time in game, in minutes."
	})
	SubSectionMessageInterface send_message();

	@SubSection
	@ConfComments({
		"A list of groups and permissions that the player must have for the process",
		"to continue. This can include group names, permission names, or neither.",
		"All of those rules must apply to the player in question, or the system will",
		"skip the processing of this definition file for the user even if the time played matches.",
		"This is useful to make different time-based rules for users who belong to different",
		"rules. Leave blank for the system to only consider time played and nothing else."
	})
	SubSectionGroupPermListInterface musthave();

	@SubSection
	@ConfComments({
		"A list of groups and permissions that the player cannot have for the process",
		"to continue. This can include group names, permission names, or neither.",
		"Unlike the must-have list, the 'canthave' only considers *any* of the groups",
		"or permissions in the list, even if the player does not have all of them. If",
		"one is true for the player, the process stops.",
		"This is useful to assign permissions or groups that are overrides for the",
		"time-based rules. You can have multiple override, and any override will apply."
	})
	SubSectionGroupPermListInterface canthave();

	@SubSection
	@ConfComments({
		"Add the user to groups/permissions.",
		"Can be one or multiple in either group or permissions lists.",
		"If none provided, no permission/group action will be done."
	})
	SubSectionGroupPermListInterface add();
}
