package chap3;

import chap2.Function;

import java.util.regex.Pattern;


/**
 * Created by zhoudunxiong on 2019/11/7.
 */
public class Example {

    static final Pattern emailPattern =
            Pattern.compile("^[a-z0-9.%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$");

//    final Function<String, Boolean> emailChecker =
//            email -> emailPattern.matcher(email).matches();

    static Function<String, Result<String>> emailChecker = email -> {
        if (email == null)
            return new Result.Failure("email is null");
        else if (email.isEmpty())
            return new Result.Failure("email is empty");
        else if (emailPattern.matcher(email).matches())
            return Result.success(email);
        else
            return Result.failure("email is valid " + email);
    };

//    static Executable validate(String mail) {
//        Result result = emailChecker.apply(mail);
//        return (result instanceof Result.Success)
//                ? () -> sendVerificationMail(mail)
//                : () -> logError(((Result.Failure) result);
//    }

    static Effect<String> sendVerificationMail =
            mail -> System.out.println("verification email send to " + mail);

    static Effect<String> logError = msg -> System.out.println("Error message logged " + msg);

    public static void main(String[] args) {
        emailChecker.apply ("this.is 日my.email").bind(sendVerificationMail, logError);
        emailChecker.apply (null).bind(sendVerificationMail, logError);
        emailChecker.apply ("").bind(sendVerificationMail, logError);
        emailChecker.apply ("john.doe@acme.com").bind(sendVerificationMail, logError);
    }

}
