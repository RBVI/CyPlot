package edu.ucsf.rbvi.cyPlot.internal.tasks;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

import edu.ucsf.rbvi.cyPlot.internal.tasks.AbstractChartController;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;


/**
 * Created by pedro_000 on 2/6/2015.
 */

public class NumberField extends TextField
{
        private final NumberFormat nf;
        private ObjectProperty<BigDecimal> number = new SimpleObjectProperty<>();
        public final BigDecimal getNumber() 				{       return number.get();        }
        public final void setNumber(BigDecimal value) 		{       number.set(value);       }
        public final void setNumber(double value) 			{       number.set(new BigDecimal(value));       }
        public ObjectProperty<BigDecimal> numberProperty() 	{       return number;       }
        AbstractChartController controller;
        
        public NumberField(AbstractChartController contrl) {            this(BigDecimal.ZERO, contrl);    }
        public NumberField(BigDecimal value, AbstractChartController contrl) {
            this(value, NumberFormat.getInstance(), contrl);
            initHandlers();
        }
        public NumberField(BigDecimal value, NumberFormat nf, AbstractChartController contrl) {
            super();
            this.nf = nf;
            controller = contrl; 
            setStyle(getStyle() + "-fx-text-alignment:RIGHT;");			// NOOP  ???
            initHandlers();
            setNumber(value);
        }
         
        private void initHandlers() {

        	// try to parse when focus is lost or RETURN is hit
            setOnAction(arg0-> {    parseAndFormatInput();  }   );
            focusedProperty().addListener((obs, old, nVal) ->{ if (!nVal.booleanValue())  parseAndFormatInput();        });
            // Set text in field if BigDecimal property is changed from outside.
              numberProperty().addListener((obs, old, nVal) ->{  textChanged(nVal);      });

            setOnKeyTyped(event ->			 // watch out for bad key input
            {
            	if (event.getCharacter().isEmpty()) return;
            	char c = event.getCharacter().charAt(0);
            	if (!(Character.isDigit(c) || c == Character.DECIMAL_DIGIT_NUMBER || ('.' == c)))
            		event.consume();
            });
        }

        private void textChanged(BigDecimal nVal) {
        	String str = nf.format(nVal);
        	setText(str);
        	controller.fieldChanged(str,getId());
	
		}
/**
 * Tries to parse the user input to a number according to the provided
 * NumberFormat
 */
        private void parseAndFormatInput() {
            try {                
            	String input = getText();
                if (input == null || input.length() == 0)     return;
                Number parsedNumber = nf.parse(input);
                BigDecimal newValue = new BigDecimal(parsedNumber.toString());
                setNumber(newValue);
                controller.fieldEdited(getId(), newValue);
                selectAll();
            } catch (ParseException ex) {
// If parsing fails keep old number
                setText(nf.format(number.get()));
            }
        }
    }

