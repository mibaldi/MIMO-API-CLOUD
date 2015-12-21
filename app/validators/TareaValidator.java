package validators;

import models.TareaModel;
import play.data.validation.Constraints;
import play.libs.F;
import play.libs.F.Tuple;

public class TareaValidator extends Constraints.Validator<String>{
	@Override
	public Tuple<String, Object[]> getErrorMessageKey() {
		return new F.Tuple<String, Object[]>("Tarea repetida", new Object[]{""}); 
	}

	@Override
	public boolean isValid(String value) {
        if(TareaModel.existe(value)) {	
            return false;
        }
        return true;
	}

}
