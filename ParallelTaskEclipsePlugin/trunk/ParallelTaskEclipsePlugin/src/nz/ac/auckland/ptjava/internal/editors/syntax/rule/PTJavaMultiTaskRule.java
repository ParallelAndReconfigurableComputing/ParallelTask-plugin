/*
 * Copyright (C) 2010 Christopher Chong, Oliver Sinnen and others.
 * 
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or 
 * combining it with Eclipse (or a modified version of that library), 
 * containing parts covered by the terms of the Eclipse Public License - v1.0, 
 * the licensors of this Program grant you additional permission to 
 * convey the resulting work. {Corresponding Source for a non-source form 
 * of such a combination shall include the source code for the parts 
 * of Eclipse used as well as that of the covered work.}
 * 
 */
package nz.ac.auckland.ptjava.internal.editors.syntax.rule;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * A custom rule for highlighting rules that look similar to <code>"TASK(4)"</code>
 * or <code>"TASK(*)"</code> or <code>"TASK(field)"</code>.
 *
 */
public class PTJavaMultiTaskRule implements IRule {

	private static final String fTaskString = "TASK(";
	private IToken fSuccessToken;
	
	/**
	 * The constructor.
	 * @param successToken the token to return upon successful match.
	 */
	public PTJavaMultiTaskRule(IToken successToken) {
		fSuccessToken = successToken;
	}
	/**
	 * Evaluates the rule by examining characters from the given CharacterScanner.
	 * Current implementation looks for rules that look similar to:
	 * <ul>
	 * 	<li>TASK(4)</li>
	 * 	<li>TASK(*)</li>
	 * 	<li>TASK(field)</li>
	 * </ul>
	 * 
	 * Uses regular expressions to evaluate the character string.
	 * 
	 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		//  TASK(*) or TASK(n) where n is a Natural number
		int count = 0;
		StringBuffer s = new StringBuffer(5);
		while (true) {
			//  read in a char
			int r = scanner.read();
			count++;
			
			//  test for end/invalid
			if (r == -1)
				break;
			
			//  append to string buffer
			s.append((char) r);
			
			//  have we read enough to test?
			if (count == 5)
				break;
		}
		if (s.toString().equals(fTaskString)) {
			StringBuffer sb= new StringBuffer(32);
			while (true) {
				int r= scanner.read();
				count++;
				if ((char)r == ')') {
					if (sb.toString().matches("([a-z[A-Z]]\\w*(\\.[a-z[A-Z]]\\w*)*)|\\*|[0-9]+"))
						return fSuccessToken;
					else
						break;
				}
				else if ((r == -1) ||
						Character.isSpaceChar((char)r)) {
					break;
				}
					
				sb.append((char)r);	
			}
		}
		//  rule does not match
		//  reset scanner
		for (int i = 0; i < count; i++)
			scanner.unread();
		
		//  return undefined token
		//  so other rules can try
		return Token.UNDEFINED;
	}

}
