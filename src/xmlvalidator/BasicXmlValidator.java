package xmlvalidator;

import java.util.regex.*;

import org.apache.commons.lang3.*;

public class BasicXmlValidator implements XmlValidator {
	BasicStringStack xmlstack = new BasicStringStack();
	BasicStringStack linestack = new BasicStringStack();

	@Override
	public String[] validate(String xmlDocument) {
		Pattern regex = Pattern.compile("<([^>]+)>");// tag finding regex
		Matcher m = regex.matcher(xmlDocument);// matcher so you can use regex
		String match;// to get tag name
		boolean flag = true; // for try, catch

		while (m.find()) {
			if ((m.group().contains("/>") || (m.group().contains("?>") || (m.group().contains("<!--")))))
				// if it is a self cloting, tag Doctype or comment
				;
			else if (m.group().contains("</")) { // if it is a closing tag
				match = StringUtils.strip(m.group(), "</>");// reduce to the tag name

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
				match = StringUtils.strip(m.group(), "</>");// remove <> mostly
				xmlstack.push(StringUtils.substringBefore(match, " ")); // push only the tag name onto the stack

				linestack.push(this.countLines(StringUtils.substring(xmlDocument, 0, m.start())) + "");
			}
		}
		if (xmlstack.getCount() > 0) {// if there is an open tag left missing closing tag
			return unclosedTag(Integer.parseInt(linestack.peek(0)), xmlstack.peek(0));
		}

		return null;
	}

	public int countLines(String input) {
		Pattern lregex = Pattern.compile("\n");
		Matcher lm = lregex.matcher(input);
		int i = 1;
		while (lm.find())
			i++;
		return i;
	}

	public int countquotes(String input) {
		Pattern qregex = Pattern.compile("\"");
		Matcher qm = qregex.matcher(input);
		int i = 0;
		while (qm.find())
			i++;
		return i;
	}

	public String[] orphanTag(int line, String name) {
		String[] temp = new String[4];
		temp[0] = "Orphan closing tag";
		temp[1] = name;
		temp[2] = line + "";
		return temp;
	}

	public String[] unclosedTag(int line, String name) {
		String[] temp = new String[4];
		temp[0] = "Unclosed tag at end";
		temp[1] = name;
		temp[2] = line + "";
		return temp;
	}

	public String[] mismatchTag(int line1, String top, int line2, String close) {
		String[] temp = new String[6];
		temp[0] = "Tag mismatch";
		temp[1] = top;
		temp[2] = line1 + "";
		temp[3] = close;
		temp[4] = line2 + "";
		return temp;
	}
}
