package io.github.mooeypoo.playingwithtime.configs.interfaces;

import java.util.Set;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultStrings;
import space.arim.dazzleconf.annote.ConfKey;

public interface SubSectionMessageInterface {
	@ConfKey("to_user")
	@ConfComments({
		"Send message directly to the user. No one else will see it.",
		"If more than one message is provided, a random one is chosen each time",
		"a user triggers the definition process."
	})
	@DefaultStrings({})
	Set<String> to_user();

	@ConfKey("to_everyone")
	@ConfComments({
		"Send message to the entire chat.",
		"If more than one message is provided, a random one is chosen each time",
		"a user triggers the definition process."
	})
	@DefaultStrings({})
	Set<String> to_everyone();

}
