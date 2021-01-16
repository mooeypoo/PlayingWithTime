import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import be.seeseemelk.mockbukkit.MockBukkit;
import io.github.mooeypoo.playingwithtime.PlayingWithTime;
import io.github.mooeypoo.playingwithtime.ProcessException;
import io.github.mooeypoo.playingwithtime.TimeCollector;
import io.github.mooeypoo.playingwithtime.configs.ConfigurationException;
import io.github.mooeypoo.playingwithtime.configs.interfaces.DefinitionConfigInterface;

public class TimeCollectorTest {
//	private PlayingWithTime plugin;

	@Before
	public void setUp() throws IOException {
	    MockBukkit.mock();
//	    this.plugin = (PlayingWithTime) MockBukkit.load(PlayingWithTime.class);
	}

	@After
	public void tearDown() throws IOException {
		MockBukkit.unmock();
		// Remove temp folder
		this.deleteFilesInTemp();
	}

	@Test
	public void testInitialization() {
		HashMap<Double, ArrayList<DefinitionConfigInterface>> map = null;
		String testType = null;
				
		testType = "single_definition";
		map = this.initializeTimeCollector(testType);
		assertTrue(
			"[" + testType + "] File inserted to map without exceptions.",
			this.areSetsEqual(map.keySet(), Sets.newHashSet(60.0))
		);

		testType = "multiple_definitions";
		map = this.initializeTimeCollector(testType);
		assertTrue(
			"[" + testType + "] File inserted to map without exceptions.",
			this.areSetsEqual(map.keySet(), Sets.newHashSet(60.0, 300.0, 600.0))
		);

		testType = "multiple_definitions_with_same_time_points";
		map = this.initializeTimeCollector(testType);
		assertTrue(
			"[" + testType + "] File inserted to map without exceptions.",
			// # Two time keys, even though there are 4 definitions
			this.areSetsEqual(map.keySet(), Sets.newHashSet(60.0, 120.0))
		);
		// Check that there are 2 defs in each timeset
		assertEquals(
			"Two definitions for time 1h",
			map.get(60.0).size(),
			2
		);
		assertEquals(
			"Two definitions for time 2h",
			map.get(120.0).size(),
			2
		);
	}

	@Test
	public void testInitializationExceptions() throws IOException {
		String testType = null;
		Path path = null;
		TimeCollector timeCollector = null;

		testType = "malformed_definition";
		path = Paths.get("src", "test", "resources", testType);
		this.copyFilesToTemp(path);

		// Bad data Exceptions shouldn't be thrown; they are dealt-with in the config-loader by resetting defaults
	    timeCollector = new TimeCollector(Paths.get("src", "test", "resources", "temp"));
		try {
			timeCollector.initialize();
		} catch (ProcessException e) {
			fail("["+ testType +"] TimeCollector initialization failed because of an exception: " + e.getMessage());
		} catch (ConfigurationException e) {
			fail("["+ testType +"] TimeCollector initialization failed because of an exception: " + e.getMessage());
		}

		assertTrue(
			"No exception is thrown.",
			true
		);
		
		this.deleteFilesInTemp();

		testType = "bad_yaml";
		path = Paths.get("src", "test", "resources", testType);
		this.copyFilesToTemp(path); // config.yml copied
		this.makeTempFileWithContent("PlayingWithTime_rank_badyaml.yml", "bad: yaml: foo: bar: baz");

		// Bad yaml throws an exception
	    timeCollector = new TimeCollector(Paths.get("src", "test", "resources", "temp"));
		try {
			timeCollector.initialize();
		} catch (ProcessException e) {
			fail("["+ testType +"] TimeCollector Process initialization failed because of an exception: " + e.getMessage());
		} catch (ConfigurationException e) {
			assertTrue(
				"Expected ConfigurationException was thrown; bubbled up from ConfigManager.",
				e.getMessage().startsWith("The yaml syntax of this file is malformed. Using defaults, instead.")
			);
			return;
		}

		fail("Expected Exception was not thrown.");
	
	}

	private HashMap<Double, ArrayList<DefinitionConfigInterface>> initializeTimeCollector(String testType) {
	    TimeCollector timeCollector = new TimeCollector(Paths.get("src", "test", "resources", testType));
		try {
			timeCollector.initialize();
		} catch (ProcessException e) {
			fail("["+ testType +"] TimeCollector initialization failed because of an exception: " + e.getMessage());
		} catch (ConfigurationException e) {
			fail("["+ testType +"] TimeCollector initialization failed because of an exception: " + e.getMessage());
		}
		return timeCollector.getFullMap();
	}
	
	private <T> Boolean areSetsEqual(Set<T> set1, Set<T> set2) {
		return set1.size() == set2.size() && set1.containsAll(set2) && set2.containsAll(set1);
	}

	private void copyFilesToTemp(Path sourcePath) throws IOException {
		String srcPathString = sourcePath.normalize().toString();
		File srcDir = new File(srcPathString);
		String destination = Paths.get("src", "test", "resources", "temp").normalize().toString();
		File destDir = new File(destination);

		FileUtils.copyDirectory(srcDir, destDir);
		System.out.println("Copying files from '" + srcDir + "' to '" + destination + "'");
	}
	
	private void makeTempFileWithContent(String filename, String content) throws IOException {
		File newFile = null;
		Path path = Paths.get("src", "test", "resources", "temp", filename);
		try {
	    	String fullPath = path.normalize().toString();
	    	System.out.println("PATH --> " + fullPath);
	        newFile = new File(fullPath);
	        if (newFile.createNewFile()) {
	          System.out.println("\n>> File created: " + newFile.getName());
	        } else {
	          System.out.println("\n>> File already exists.");
	        }
		} catch (IOException e) {
		    System.out.println("An error occurred.");
		    e.printStackTrace();
		}
		if (newFile != null) {
	        FileWriter writer = new FileWriter(newFile);
	        writer.write(content);
	        writer.close();
		}
	}
	private void deleteFilesInTemp() throws IOException {
		String path = Paths.get("src", "test", "resources", "temp").normalize().toString();
		FileUtils.cleanDirectory(new File(path));
		System.out.println("Deleting contents of folder '" + path + "'");
	}
}
