package validators;

import javax.validation.ConstraintValidator;

import play.data.validation.Constraints;
import play.libs.F.Tuple;


public class NumeroTelefonoValidator extends Constraints.Validator<String> 
		implements ConstraintValidator<NumeroTelefono, String> {
	
	java.util.regex.Pattern regex;

	@Override
	public void initialize(NumeroTelefono annotation) {
		regex = java.util.regex.Pattern.compile("[0-9]{9}");
	}

	@Override
	public Tuple<String, Object[]> getErrorMessageKey() {
		return null;
	}

	@Override
	public boolean isValid(String value) {
        return value==null || regex.matcher(value).matches() ;
	}

}
