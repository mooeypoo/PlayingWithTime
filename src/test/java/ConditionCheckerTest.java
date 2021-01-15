import static org.junit.Assert.*;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import io.github.mooeypoo.playingwithtime.ConditionChecker;
import io.github.mooeypoo.playingwithtime.PlayingWithTime;

public class ConditionCheckerTest {
	private ServerMock server;
	private PlayingWithTime plugin;

	@Before
	public void setUp() {
	    this.server = MockBukkit.mock();
	    this.plugin = (PlayingWithTime) MockBukkit.load(PlayingWithTime.class);

	    // Add a list of permission definitions to the server, default false
	    Set<String> initialPermissions = Sets.newHashSet(
	    		"test.perm1",
	    		"test.perm2",
	    		"test.perm3",
	    		"test.perm4",
	    		"test.perm5",
	    		"group.test.group1",
	    		"group.test.group2",
	    		"group.test.group3",
	    		"group.test.group4",
	    		"group.test.group5"
	    );
	    
	    for (String pName : initialPermissions) {
			server.getPluginManager().addPermission(new Permission(
				pName,
				PermissionDefault.FALSE
			));
	    }
	}

	@After
	public void tearDown() {
		MockBukkit.unmock();
	}

	@Test
	public void isStringEmptyTest() {
		assertEquals(
			"isStringEmpty: Check for null",
			ConditionChecker.isStringEmpty(null),
			true
		);
		assertEquals(
			"isStringEmpty: Empty string",
			ConditionChecker.isStringEmpty(""),
			true
		);
		assertEquals(
			"isStringEmpty: Spaces",
			ConditionChecker.isStringEmpty("                   "),
			true
		);
		assertEquals(
			"isStringEmpty: Spaces, with text",
			ConditionChecker.isStringEmpty("             foo      "),
			false
		);
	}

	@Test
	public void playerMustHaveAllTest() {
		PlayerMock player = this.server.addPlayer();

		// Add some permissions/groups to the user
		player.addAttachment(plugin, "test.perm1", true);
		player.addAttachment(plugin, "group.test.group1", true);
		
		// Test that the user has given permission
		assertEquals(
			"User has all permissions.",
			ConditionChecker.doesPlayerHaveAll(
				Sets.newHashSet("test.perm1"),
				player, false
			),
			true
		);
		
		// Test perm that the user doesn't have
		assertEquals(
			"User is missing one of the requested permission",
			ConditionChecker.doesPlayerHaveAll(
					Sets.newHashSet("test.perm1", "test.perm2"),
				player, false
			),
			false
		);

		// Test that the user has given groups
		assertEquals(
			"User has all groups.",
			ConditionChecker.doesPlayerHaveAll(
				Sets.newHashSet("test.group1"),
				player, true // group
			),
			true
		);

		// Test perm that the user doesn't have
		assertEquals(
			"User is missing one of the requested groups",
			ConditionChecker.doesPlayerHaveAll(
				Sets.newHashSet("test.group1", "test.group2"),
				player, true // group
			),
			false
		);
	}

	@Test
	public void playerMustHaveNoneTest() {
		PlayerMock player = this.server.addPlayer();

		// Add some permissions/groups to the user
		player.addAttachment(plugin, "test.perm1", true);
		player.addAttachment(plugin, "group.test.group1", true);
		
		assertEquals(
			"User has none of these permissions: true",
			ConditionChecker.doesPlayerHaveNone(
				Sets.newHashSet("test.perm3", "test.perm4"),
				player, false
			),
			true
		);
		
		// Test perm that the user doesn't have
		assertEquals(
			"User has only one of these permission: false",
			ConditionChecker.doesPlayerHaveNone(
					Sets.newHashSet("test.perm1", "test.perm2"),
				player, false
			),
			false
		);

		assertEquals(
			"User has none of these groups: true",
			ConditionChecker.doesPlayerHaveNone(
				Sets.newHashSet("test.group3", "test.group4"),
				player, true
			),
			true
		);
			
		// Test perm that the user doesn't have
		assertEquals(
			"User has one of these groups: false",
			ConditionChecker.doesPlayerHaveNone(
					Sets.newHashSet("test.group1", "test.group2"),
				player, true
			),
			false
		);
	}

//	@Test
//	public void PlayerTimeTest() {
//		// Apparently, "player.getStatistic" is not implemented in mockbukkit
//		// https://github.com/seeseemelk/MockBukkit/blob/5bb5187ec32de484d8d2f979d648060d67da3792/src/main/java/be/seeseemelk/mockbukkit/entity/PlayerMock.java#L1183
//		// PlayerMock player = this.server.addPlayer();
//		// System.out.println("time in game: " + ConditionChecker.getPlayerTimeInMinutes(player));
//	}

}
