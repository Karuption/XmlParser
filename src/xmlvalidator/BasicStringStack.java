package xmlvalidator;

import java.util.*;

public class BasicStringStack implements StringStack {
	ArrayList<String> stack = new ArrayList<String>();

	@Override
	public void push(String item) {
		stack.add(0, item);
	}

	@Override
	public String pop() {
		String temp;
		try {
			temp = new String(this.peek(0));
			stack.remove(0);
		} catch (Exception e) {
			return null;
		}
		return temp;
	}

	@Override
	public String peek(int position) {
		return stack.get(position);
	}

	@Override
	public int getCount() {
		return stack.size();
	}

}
