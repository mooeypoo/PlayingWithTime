package io.github.mooeypoo.playingwithtime.configs.interfaces;

import java.util.Set;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.annote.ConfDefault.DefaultStrings;

public interface MainConfigInterface {
	@ConfKey("definitions")
	@ConfComments({
		"A list of ranks to use. Each rank name will be defined in a separate file that's defined",
		"in a separate config, determining the in-game time to activate and the resulting permission",
		"or groups to be added to the player when that time is met."
	})
	@DefaultStrings({"1day"})
	Set<String> definitions();

	@SubSection
	@ConfComments({
		"A list of commands representing the way to add a permission and/or group to a user.",
		"The defaults utilize LuckPerm commands, but you can change this to utilize any other",
		"method of updating group or permission to your users.",
		"Make sure you use the correct placeholders:",
		"Available placeholders for commands:",
		"- %playername% Player name",
		"- %timeingame% Player's time in game, in minutes.",
		"- %typename% for the name of the permission or group that you want your user to be added to.",
		"You can add multiple commands -- all will be run when the process decides to add a group or permission",
		"to a player.",
		"NOTE: If no commands are specified, the system will NOT be able to add groups or permissions!"
	})
	SubSectionGroupPermCommandsInterface commands_definition_add();

}
