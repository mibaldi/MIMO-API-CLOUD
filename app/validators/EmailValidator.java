package validators;

import java.util.ArrayList;

import javax.validation.ConstraintValidatorContext;

import models.EmailModel;
import models.TareaModel;
import play.data.validation.Constraints;
import play.libs.F;
import play.libs.F.Tuple;

public class EmailValidator extends Constraints.Validator<String>{
	@Override
	public Tuple<String, Object[]> getErrorMessageKey() {
		return new F.Tuple<String, Object[]>("invalid_email", new Object[]{""}); 
	}
	@Override
	public boolean isValid(String value) {
		
		String regex="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		boolean res=true;
		if (!value.matches(regex)){
			res=false;
		}else{
			if (EmailModel.existe(value)) {
				res=false;
			}
		}
		return res;
	}

}
