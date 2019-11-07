package chap3;

import chap2.Function;

import java.util.regex.Pattern;


/**
 * Created by zhoudunxiong on 2019/11/7.
 */
public class Example {

    final Pattern emailPattern =
            Pattern.compile("^[a-z0-9.%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$");

//    final Function<String, Boolean> emailChecker =
//            email -> emailPattern.matcher(email).matches();

    final Function<String, Result> emailChecker = email -> {
        if (email == null)
            return new Result.Failure("email is null");
        else if (email.isEmpty())
            return new Result.Failure("email is empty");
        else if (emailPattern.matcher(email).matches())
            return new Result.Success();
        else
            return new Result.Failure("email is valid " + email);
    };

    void testMail(String mail) {
        Result result = emailChecker.apply(mail);
        if (result instanceof Result.Success) {
            sendVerificationMail(mail);
        } else {
            logError(((Result.Failure) result).getMessage());
        }
    }

    void sendVerificationMail(String mail) {
        System.out.println("verification email send to " + mail);
    }

    void logError(String msg) {
        System.out.println("Error message logged " + msg);
    }
}
