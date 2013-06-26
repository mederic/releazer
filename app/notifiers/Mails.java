package notifiers;

import play.mvc.*;
import models.Release;
import models.User;
import play.Play;

public class Mails extends Mailer {

    public static void releasePublished(User user, Release release) {
        String noReplyAdress = Play.configuration.getProperty("noReplyAdress", "noreply@noreply.com");
        setFrom("NoReply <" + noReplyAdress + ">");
        setSubject("Releazer : [" + release.project.name + "] a new release has been published !");
        addRecipient(user.email);
        send(user, release);
    }
}