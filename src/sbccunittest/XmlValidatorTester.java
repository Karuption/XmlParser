package sbccunittest;

import static org.junit.Assert.*;

import java.io.*;

import org.apache.commons.io.*;
import org.junit.*;

import xmlvalidator.*;

public class XmlValidatorTester {

	BasicXmlValidator validator;
	BasicStringStack stack;

	public static int totalScore = 0;
	public static int extraCredit = 0;

	@BeforeClass
	public static void beforeTesting() {
		totalScore = 0;
		extraCredit = 0;
	}

	@AfterClass
	public static void afterTesting() {
		System.out
				.println("Estimated score (assuming no late penalties, etc.) = "
						+ totalScore);
		System.out
				.println("Estimated extra credit (assuming on time submission) = "
						+ extraCredit);
	}

	@Before
	public void setUp() throws Exception {
		stack = new BasicStringStack();
		validator = new BasicXmlValidator();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPush() {
		stack.push("B");
		assertEquals("B", stack.peek(0));
		totalScore += 2;
	}

	@Test
	public void testPop() {
		stack.push("C");
		stack.push("D");
		assertEquals(stack.pop(), "D");
		stack.pop();
		assertEquals(null, stack.pop());
		totalScore += 3;
	}

	@Test
	public void testExercise() {
		stack.push("A");
		stack.push("B");
		stack.push("C");
		stack.push("D");
		assertEquals(stack.peek(0), "D");
		assertEquals(4, stack.getCount());
		assertEquals("A", stack.peek(3));
		assertEquals(stack.pop(), "D");
		assertEquals(3, stack.getCount());

		stack.pop();
		stack.pop();
		assertEquals("A", stack.pop());
		assertEquals(0, stack.getCount());

		stack.pop();
		stack.pop();
		assertEquals(null, stack.pop());
		totalScore += 5;
	}

	@Test
	public void testValidFile() throws IOException {
		String xmlDocument = FileUtils.readFileToString(new File(
				"TestFile2.xml"));
		String[] result = validator.validate(xmlDocument);
		assertNull(result);
		totalScore += 10;
	}

	@Test
	public void testBigValidFile() throws IOException {
		String xmlDocument = FileUtils.readFileToString(new File(
				"TestFile4.xml"));
		String[] result = validator.validate(xmlDocument);
		assertNull(result);
		totalScore += 5;
	}

	@Test
	public void testOrphanClosingTag() throws IOException {
		String xmlDocument = FileUtils.readFileToString(new File(
				"TestFile6.xml"));
		String[] result = validator.validate(xmlDocument);
		assertEquals("Orphan closing tag", result[0]);
		assertEquals("projectDescription", result[1]);
		assertEquals("34", result[2]);
		totalScore += 5;
	}

	@Test
	public void testUnclosedTag() throws IOException {
		String xmlDocument = FileUtils.readFileToString(new File(
				"TestFile1.xml"));
		String[] result = validator.validate(xmlDocument);
		assertEquals("Tag mismatch", result[0]);
		assertEquals("name", result[1]);
		assertEquals("24", result[2]);
		assertEquals("buildCommand", result[3]);
		assertEquals("27", result[4]);
		totalScore += 10;
	}

	@Test
	public void testUnclosedTagAtEnd() throws IOException {
		String xmlDocument = FileUtils.readFileToString(new File(
				"TestFile3.xml"));
		String[] result = validator.validate(xmlDocument);
		assertEquals("Unclosed tag at end", result[0]);
		assertEquals("natures", result[1]);
		assertEquals("29", result[2]);
		totalScore += 10;
	}

	@Test
	public void testAttributeNotQuoted() throws IOException {
		String xmlDocument = FileUtils.readFileToString(new File(
				"TestFile5.xml"));
		String[] result = validator.validate(xmlDocument);
		assertEquals("Attribute not quoted", result[0]);
		assertEquals("versionInfo", result[1]);
		assertEquals("35", result[2]);
		assertEquals("copyright", result[3]);
		assertEquals("35", result[4]);
		extraCredit += 3;
	}

}