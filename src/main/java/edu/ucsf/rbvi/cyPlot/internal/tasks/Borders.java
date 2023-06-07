package edu.ucsf.rbvi.cyPlot.internal.tasks;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class Borders
{
	static public Border redBorder = new Border(new BorderStroke(Color.RED, 
					BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))	);

	static public Border thinRedBorder = new Border(new BorderStroke(Color.RED, 
					BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))	);

	static public Border blueBorder1 = new Border(new BorderStroke(Color.BLUE, 
			BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))	);

	static public Border blueBorder2 = new Border(new BorderStroke(Color.BLUE, 
			BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))	);

	static public Border blueBorder5 = new Border(new BorderStroke(Color.BLUE, 
					BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5))	);

	static public Border greenBorder = new Border(new BorderStroke(Color.GREEN, 
					BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))	);

	static public Border cyanBorder = new Border(new BorderStroke(Color.CYAN, 
					BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))	);

	static public Border magentaBorder = new Border(new BorderStroke(Color.MAGENTA, 
					BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5))	);

	static public Border yellowBorder = new Border(new BorderStroke(Color.YELLOW, 
					BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5))	);
	static public Border thinGold = new Border(new BorderStroke(Color.GOLD, 
					BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))	);

	static public Border etchedBorder = new Border(
					new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5)),
					new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)),
					new BorderStroke(Color.WHEAT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))
					);

	static public Border thinEtchedBorder = new Border(
					new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3)),
					new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1)),
					new BorderStroke(Color.WHEAT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))
					);

	static public Border lineBorder = new Border(new BorderStroke(Color.DARKGRAY, 
					BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))	);

	static public Border dashedBorder = new Border(new BorderStroke(Color.DARKGRAY, 
					BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(2))	);

	static public Border emptyBorder = new Border(new BorderStroke(null, 
					BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(2))	);

	static public Border coloredBorder(String webColor)
	{
		return new Border(new BorderStroke(Color.web(webColor), 
					BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))	);
	}
//	public static Border getValidationBorder(ValidationState validationState)
//	{
//		if (validationState == ValidationState.ERROR)		return thinRedBorder;
//		if (validationState == ValidationState.REQUIRED)	return thinRedBorder;
//		if (validationState == ValidationState.WARNING)		return thinGold;
//		return emptyBorder;
//	}

}
