<?php include_once("header.php"); ?>
<h1>About Bugeater</h1>
<h3>Introduction</h3>
<p>
	Bugeater is an open source software development issue tracker.  This system
	can help organizations track bugs (defects) in software as well as requests
	for new functionality and requests for changes in existing functionality.
</p>
<p>
	Bugeater was designed and built by Philip A. Chapman.  The logo was designed
	by Michael Worcester.  Creative input was provided by Andrew Lombardi and
	Igor Vaynberg.  It was built using the <a href="http://wicketframework.org">
	Wicket web</a>, <a href="http://springframework.org">Spring inversion of
	control</a>, and <a href="http://hibernate.org">hibernate ORM</a>
	frameworks.
</p>
<p>
	The bugeater project is hosted at <a
	href="https://developer.berlios.de/projects/bugeater/">
	https://developer.berlios.de/projects/bugeater/</a>  You can find the source
	code, mailing lists, and other forms of support by clicking on the links to
	the right.
</p>
<h3>Strengths</h3>
<p>
	Bugeater only took a few days to create.  Wicket's fantastic that way.
	Bugeater is in use in a production environment backed by a PostgreSQL
	database.  It is usable and useful in its current state.  Below, you will
	find a list of its most prominent features:
	<ul>
		<li>
			Bugeater is designed for simplicity.  The user interface is simple and
			easy to use.
		</li>
		<li>
			Notes entered by users about issues are entered in wiki syntax for
			easy formatting.
		</li>
		<li>Issues may have attachments.  This is useful for screenshots, output samples, etc.</li>
		<li>
			Users may assign themselves as &quot;watchers&quot; of an issue.
			Watchers get email notification whenever the status of an issue
			changes or a new note is posted.
		</li>
		<li>The text of issue titles and the body of notes are searchable.</li>
		<li>Bugeater supports role based security.</li>
		<li>
			User authentication is built upon a pluggable architecture.  Because
			of this, authentication can come from external systems, such as
			databases, single sign-on frameworks, or HR systems.
		</li>
		<li>
			Because bugeater uses hibernate, any RDBMS supported by hibernate may
			be used as a backend for bugeater.  This includes most, if not all,
			RDBMS systems that have a JDBC driver available.
		</li>
	</ul>
</p>
<h3>Shortcomings</h3>
<p>
	There are some shortcomings in bugeater that the developers are aware of and
	will be addressing soon.
	<ul>
		<li>
			Attachments are downloaded to the client computer with the correct
			mime type, but not the right name.
		</li>
		<li>
			Bugeater does not support different role assignments per project.  A
			developer for project &quot;A&quot; is a developer for all projects.
		</li>
		<li>
			In the interest of keeping the interface simple, we made the search
			form too simple.  There is currently no way to search with multiple
			parameters.  We wish to fix this soon, but will work hard to keep
			bugeater from becoming a complex, bewildering array of toggles, text
			boxes, and option lists that software developers somehow seem to
			gravitate to.
		</li>
		<li>
			There is no good authentication system built in by default.  The
			system currently requires that whoever uses bugeater implement the
			authentication interface so that it will fit their needs.  The
			default authentication system built into bugeater is useful for
			testing only and would not be good for use in production systems.
		</li>
		<li>
			Related to the previous shortcoming is that there is currently no user
			administrative capabilities. There are plans to extend the
			authentication plugin architecture to allow for user administration by
			an administrator logged into bugeater, shoud such capabilities be
			desired.  This is an item that must be addressed in order to make
			bugeater a good stand-alone issue tracking system.
		</li>
	</ul>
</p>
<?php include_once("footer.php"); ?>