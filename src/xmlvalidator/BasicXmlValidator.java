package xmlvalidator;

import java.util.regex.*;

import org.apache.commons.lang3.*;

public class BasicXmlValidator implements XmlValidator {
	private BasicStringStack xmlstack = new BasicStringStack();
	private BasicStringStack linestack = new BasicStringStack();

	@Override
	public String[] validate(String xmlDocument) {
		Pattern regex = Pattern.compile("<([^>]+)>");// tag finding regex
		Matcher m = regex.matcher(xmlDocument);// matcher so you can use regex
		String match;// to get tag name
		boolean flag = true; // for try, catch
		@SuppressWarnings("unused")
		String[] quotescheck;

		while (m.find()) {
			match = StringUtils.strip(m.group(), "</>");// remove </>
			if ((m.group().contains("/>") || (m.group().contains("?>") || (m.group().contains("<!--"))))) {
				// if it is a self cloting, tag Doctype or comment
				if (match.contains("=")) {
					if (this.validateAttributes(match.split(" "), match, m, xmlDocument) != null)
						return this.validateAttributes(match.split(" "), match, m, xmlDocument);
				}
			} else if (m.group().contains("</")) { // if it is a closing tag
				try {
					flag = match.equals(xmlstack.peek(0)); // try equating the tags
				} catch (Exception e) {
					return this.orphanTag(this.countLines(StringUtils.substring(xmlDocument, 0, m.start())), match);
					// if xmlstack is empty there is an orphan closing tag
				}

				if (flag) { // if they do equal it is the right closing tag
					xmlstack.pop();
					linestack.pop();
				} else if (xmlstack.getCount() == 0) { // if there are no opening tags to the closing
					return this.orphanTag(this.countLines(StringUtils.substring(xmlDocument, 0, m.start())), match);
				} else {// tag missmatch... openingtag != closingtag
					match = StringUtils.strip(m.group(), "</>");// get the tag name
					return this.mismatchTag(Integer.parseInt(linestack.peek(0)), xmlstack.peek(0),
							this.countLines(StringUtils.substring(xmlDocument, 0, m.start())), match);
				}
			} else {
				xmlstack.push(StringUtils.substringBefore(match, " ")); // push only the tag name onto the stack

				linestack.push(this.countLines(StringUtils.substring(xmlDocument, 0, m.start())) + "");
			}
		}
		if (xmlstack.getCount() > 0) {// if there is an open tag left missing closing tag
			return unclosedTag(Integer.parseInt(linestack.peek(0)), xmlstack.peek(0));
		}

		return null;
	}

	private int countLines(String input) {
		Pattern lregex = Pattern.compile("\n");
		Matcher lm = lregex.matcher(input);
		int i = 1;
		while (lm.find())
			i++;
		return i;
	}

	private String[] validateAttributes(String quotescheck[], String match, Matcher m, String xmlDocument) {
		for (int i = 1; i < quotescheck.length; i++) { // for attribute i
			if (quotescheck[i].contains("=")) {// if it needs "'s
				if (!(quotescheck[i].contains("=\"") && quotescheck[i].contains("\""))) {// if doesnt have two
					int mult = 0;// multiple line counter
					// counts possible lines
					mult = this.countLines(StringUtils.substringBefore(match, quotescheck[i])) - 1;
					int temp = this.countLines(StringUtils.substring(xmlDocument, 0, m.start()));
					// counts lines up to tag
					return this.notquoted(temp, StringUtils.substringBefore(match, " "), (temp + mult),
							StringUtils.substringBefore(quotescheck[i], "="));
				}
			}
		}
		return null;
	}

	private String[] orphanTag(int line, String name) {
		String[] temp = new String[4];
		temp[0] = "Orphan closing tag";
		temp[1] = name;
		temp[2] = line + "";
		return temp;
	}

	private String[] unclosedTag(int line, String name) {
		String[] temp = new String[4];
		temp[0] = "Unclosed tag at end";
		temp[1] = name;
		temp[2] = line + "";
		return temp;
	}

	private String[] mismatchTag(int line1, String top, int line2, String close) {
		String[] temp = new String[6];
		temp[0] = "Tag mismatch";
		temp[1] = top;
		temp[2] = line1 + "";
		temp[3] = close;
		temp[4] = line2 + "";
		return temp;
	}

	private String[] notquoted(int line, String tag, int aline, String Attribute) {
		String[] temp = new String[6];
		temp[0] = "Attribute not quoted";
		temp[1] = tag;
		temp[2] = line + "";
		temp[3] = Attribute;
		temp[4] = aline + "";
		return temp;
	}
}
