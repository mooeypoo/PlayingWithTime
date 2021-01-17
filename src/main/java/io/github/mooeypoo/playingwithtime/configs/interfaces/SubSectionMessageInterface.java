package io.github.mooeypoo.playingwithtime.configs.interfaces;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultString;
import space.arim.dazzleconf.annote.ConfKey;

public interface SubSectionMessageInterface {
	@ConfKey("to_user")
	@ConfComments({
		"Send message directly to the user. No one else will see it."
	})
	@DefaultString("")
	String to_user();

	@ConfKey("to_everyone")
	@ConfComments({
		"Send message to the entire chat."
	})
	@DefaultString("")
	String to_everyone();

}
