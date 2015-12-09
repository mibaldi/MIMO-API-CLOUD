package validators;

import javax.validation.ConstraintValidator;

import play.data.validation.Constraints;
import play.libs.F;
import play.libs.F.Tuple;
import util.passwordInter;

public class PasswordValidator extends Constraints.Validator<String> implements ConstraintValidator<passwordInter,String>  {
	public String regex="^[a-zA-Z0-9]{8,20}";
@Override
public Tuple<String, Object[]> getErrorMessageKey() {
	return new F.Tuple<String, Object[]>("Contrasenia no valida", new Object[]{""}); 
	}
@Override
public boolean isValid(String value) {
return value.matches(regex);
	//return !value.startsWith("a");
}
@Override
public void initialize(passwordInter arg0) {
	// TODO Auto-generated method stub
	
} 
}
