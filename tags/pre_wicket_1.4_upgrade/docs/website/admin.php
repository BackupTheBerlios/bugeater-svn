<?php include_once("header.php"); ?>
<h1>Building and Installing Bugeater</h1>
<h3>About Building an Installing</h3>
<p>
	We have not yet released a packaged version of Bugeater.  In order to
	install and use Bugeater, you must compile it, then install it in your
	java web servlet container.
</p>
<p>
	This documentation is currently very basic.  We hope to have much more
	documentation on this page soon.  Feel free to email the user list with
	any questions you may have.  Also, the authors can often be found in the
	<a href="irc://irc.freenode.net:6667/##wicket">wicket IRC channel</a> on
	irc.freenode.net.
</p>
<h3>Setup</h3>
<p>
	You will need to have at least version 1.5 of the <a href="http://java.sun.com">Java Development
	Kit<sup>tm</sup></a> installed to compile bugeater out of trunk.  A version of bugeater that uses wicket 1.3beta and that will compile against JDK 1.4 is available in http://svn.berlios.de/svnroot/repos/bugeater/branches/bugeater_wicket_1.3.  You will also need to download and install
	<a href="http://maven.apache.org/">maven2</a> for dependency resolution and
	build management.  Get the source of bugeater <a
	href="https://developer.berlios.de/svn/?group_id=6908">here</a>.
</p>
<h3>Implementing the Authentication Interface</h3>
<p>
	After checking out the source, open up a console and change directory to the
	root directory of bugeater's code.  If you are using the Eclipse IDE, run
	the command <code>mvn eclipse:eclipse</code>.  If you are using the IDEA
	IDE, run the command <code>mvn idea:idea</code>.  These commands will
	download all dependencies into your local cache and set up the bugeater
	project for development in your IDE of choice.  You must implement the
	following interfaces:
	<ul>
		<li>
			bugeater.bean.IUserBean - A basic bean that holds user information,
			such as name and login.
		</li>
		<li>
			bugeater.service.UserService - A service that is used to obtain user
			objects by login, role, id, etc.  In the future, this interface will
			be expanded to include ways to minipulate user data.
		</li>
		<li>
			bugeater.service.AuthenticationService - The implementation of this
			service is responsible for the actual authentication and returning
			roles for a login.
		</li>
	</ul>
</p>
<p>
	Once you have implemented the interfaces above, you will want to edit the
	bugeater-context.xml file so that spring provides instances of your
	implementation for beans with ids &quot;authenticationService*quot; and
	&quot;userService&quot;.  See the spring documentation to determine exactly
	how this works and for the format of the bugeater-context.xml file.
</p>
<h3>Building</h3>
<p>
	After implementing the user and authentication services, you can then
	compile bugeater.  Open up a console and change directory to the root
	directory of bugeater's code.  Run the command <code>mvn package</code>.
	This command will cause maven to download all dependencies into your local
	cache, compile bugeater, and create a war file in the target directory.
</p>
<?php include_once("footer.php"); ?>
