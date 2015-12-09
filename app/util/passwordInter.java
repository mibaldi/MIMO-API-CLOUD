package util;



import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import validators.PasswordValidator;


@Target({java.lang.annotation.ElementType.FIELD})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface passwordInter {
	String message() default "invalid_password"; Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	}
