package xmlvalidator;

import java.util.*;
import java.util.regex.*;

import org.apache.commons.lang3.*;

public class BasicXmlValidator implements XmlValidator {
	BasicStringStack xmlstack = new BasicStringStack();
	BasicStringStack linestack = new BasicStringStack();

	@Override
	public String[] validate(String xmlDocument) {
		Pattern regex = Pattern.compile("(<[\\w =':\\./]+)>|\n");
		Matcher m = regex.matcher(xmlDocument);
		int linecount = 1;

		while (m.find()) {
			if (m.group().equals("\n"))
				linecount++;
			if (m.group().contains("<")) {
				if (m.group().contains("</")) {
					if (StringUtils.strip(m.group(), "</>").equals(xmlstack.peek(0))) {
						xmlstack.pop();
						linestack.pop();
					}
				} else {
					xmlstack.push(StringUtils.strip(m.group(), "<>"));
					linestack.push(linecount + "");
				}
			}
		}
		System.out.println(linecount + "");
		return null;
	}

	public String[] orphanTag(int line, String name) {
		ArrayList<String> temp = new ArrayList<String>();
		temp.add("Orphan closing tag");
		temp.add(name);
		temp.add(line + "");
		return (String[]) temp.toArray();
	}

	public String[] unclosedTag(int line, String name) {
		ArrayList<String> temp = new ArrayList<String>();
		temp.add("Unclosed tag at end");
		temp.add(name);
		temp.add(line + "");
		return (String[]) temp.toArray();
	}

	public String[] mismatchTag(int line1, String top, int line2, String close) {
		ArrayList<String> temp = new ArrayList<String>();
		temp.add("Tag mismatch");
		temp.add(top);
		temp.add(line1 + "");
		temp.add(close);
		temp.add(line2 + "");
		return (String[]) temp.toArray();
	}
}
