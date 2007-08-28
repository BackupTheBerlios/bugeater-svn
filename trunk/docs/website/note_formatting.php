<?php include_once("header.php"); ?>
					<div id="left">
	<h2>Note Style Markup</h2>
	<h3>Text formatting rules</h3>
	<p>
		Text may contain the following macros to format text.  This includes
		simple macros for <b>bold</b> and <i>italic</i> as well as special
		macros for source code.
	</p>
	<h3>Simple text macros</h3>
	<table style="margin: 20px; vertical-align: top;">
		<tbody>
			<tr><th>Mnemonic</th><th>Rendering</th><th>Comment</th></tr>
			<tr>
				<td>1 Title</td><td><h1>Title</h1></td>
				<td>a style 1 heading (major heading)</td>
			</tr>
			<tr>
				<td>1.1 Title</td><td><h2>Title</h2></td>
				<td>a style 1.1 heading (minor heading)</td>
			</tr>
			<tr>
				<td>* text</td>
				<td><ul style="list-style-type: disc;"><li>text</li></ul></td>
				<td>a list item with a disc as the list item marker</td>
			</tr>
			<tr>
				<td>- text</td>
				<td><ul style="list-style-type: square;"><li>text</li></ul></td>
				<td>a list item with a box as the list item marker</td>
			</tr>
			<tr>
				<td>1. text</td>
				<td>
					<ol style="list-style-type: decimal;"><li>text</li></ol>
				</td>
				<td>an enumerated list enumerated by integer numbers</td>
			</tr>
			<tr>
				<td>a. text</td>
				<td>
					<ol style="list-style-type: lower-alpha;">
						<li>text</li>
					</ol>
				</td>
				<td>
					an enumerated list enumerated by lower-case alphabet
					characters
				</td>
			</tr>
			<tr>
				<td>A. text</td>
				<td>
					<ol style="list-style-type: upper-alpha;">
						<li>text</li>
					</ol>
				</td>
				<td>
					an enumerated list enumerated by upper-case alphabet
					characters
				</td>
			</tr>
			<tr>
				<td>i. text</td>
				<td>
					<ol style="list-style-type: lower-roman;">
						<li>text</li>
					</ol>
				</td>
				<td>
					an enumerated list enumerated by lower-case roman numerals
				</td>
			</tr>
			<tr>
				<td>I. text</td>
				<td>
					<ol style="list-style-type: upper-roman;">
						<li>text</li>
					</ol>
				</td>
				<td>
					an enumerated list enumerated by upper-case roman numerals
				</td>
			</tr>
			<tr>
				<td>__text__</td><td><b class="bold">text</b></td>
				<td>simple bold text</td>
			</tr>
			<tr>
				<td>~~text~~</td><td><i class="italic">text</i></td>
				<td>simple italic text</td>
			</tr>
			<tr>
				<td>--text--</td>
				<td><strike class="strike">text</strike></td>
				<td>strike through text</td>
			</tr>
			<tr>
				<td>(empty line)</td><td>&nbsp;</td>
				<td>produces a new paragraph</td>
			</tr>
			<tr>
				<td>\\</td><td>&nbsp;</td>
				<td>creates a line break, please use sparingly!</td>
			</tr>
			<tr>
				<td>http://snipsnap.org/</td>
				<td>
					<a href="http://snipsnap.org/">http://snipsnap.org/</a>
				</td>
				<td>
					creates a link to an external resource, special characters
					that come after the URL and are not part of it must be
					separated with a space.
				</td>
			</tr>
			<tr>
				<td>\X</td><td>X</td>
				<td>escape special character X (i.e. {)</td>
			</tr>
		</tbody>
	</table>
	<h3>Advanced Macros</h3>
	<table style="margin: 20px; vertical-align: top;">
		<tbody>
			<tr><td colspan="2"><hr/></td></tr>

			<!-- api macro -->
			<tr><td><b>Macro</b></td><td>api</td></tr>
			<tr>
				<td><b>Parameters</b></td>
				<td>
					<ol>
						<li>
							class name, e.g. java.lang.Object
						</li>
						<li>
							mode, e.g. Java12, Ruby, defaults to java
							<i>(optional)</i>
						</li>
					</ol>
				</td>
			</tr>
			<tr>
				<td><b>Description</b></td>
				<td>
					Generates links to Java or Ruby API documentation.
					<strong>
						Note that this macro only supports Java 1.4.1, 1.3.1,
						1.2, and j2ee 1.3.  Java 1.5 is not supported.
					</strong>
				</td>
			</tr>
			<tr>
				<td><b>Example</b></td>
				<td>
					{api:java.lang.Object} or {api:java.lang.Object@java12}
				</td>
			</tr>
			<tr><td colspan="2"><hr/></td></tr>
	
			<!-- api-doc macro -->
			<tr><td><b>Macro</b></td><td>api-docs</td></tr>
			<tr><td><b>Parameters</b></td><td></td></tr>
			<tr>
				<td><b>Description</b></td>
				<td>
					Displays a list of known online API documentations and
					mappings for use with the api macro.
				</td>
			</tr>
			<tr>
				<td><b>Example</b></td>
				<td>
					{api-docs}
				</td>
			</tr>
			<tr><td colspan="2"><hr/></td></tr>
	
			<!-- code macro -->
			<tr><td><b>Macro</b></td><td>code</td></tr>
			<tr>
				<td><b>Parameters</b></td>
				<td>
					<ol>
						<li>
							syntax highlighter to use, defaults to java
							<i>(optional)</i>
						</li>
					</ol>
				</td>
			</tr>
			<tr>
				<td><b>Description</b></td>
				<td>
					Displays a snippet of code with syntax highlighting, for
					example Java, XML and SQL. The none type will do nothing
					and is useful for unknown code types.
				</td>
			</tr>
			<tr>
				<td><b>Example</b></td>
				<td>
					The input:
					<div style="margin: 5px 20px 5px 20px;">
						{code}<br/>
						String s = new String();<br/>
						System.out.println(s);<br/>
						{code}<br/>
					</div>
					will output similar to:
<pre style="margin: 5px 20px 5px 20px">
String s = new String();
System.out.println(s);
</pre>
				</td>
			</tr>
			<tr><td colspan="2"><hr/></td></tr>
		</tbody>
	</table>
					</div>
<?php include_once("footer.php"); ?>
