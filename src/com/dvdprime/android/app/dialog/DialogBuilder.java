package com.dvdprime.android.app.dialog;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.task.ExceptionHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;

/**
 * DialogBuilder is a class for building select, 
 * message, quick fill, date picker, month year picker, 
 * count picker, confirm dialogs .
 */
public final class DialogBuilder {

	/**
	 * Hidden DialogBuilder constructor.
	 */
	private DialogBuilder() {
	}
	
	public static Dialog createSelectDialog(final Activity activity, final String title,
			final String[] items, final Integer checked,
			final ISetSelectValue setSelectValue) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		if (title != null) {
			builder.setTitle(title);
		}
		builder.setSingleChoiceItems(items, checked,
				new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface d, final int i) {
						d.dismiss();
						setSelectValue.setValue(items[i], i);
					}
				});
		builder.setNegativeButton(activity.getResources().getString(R.string.button_cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface d, final int w) {
						d.dismiss();
					}
				});
		final AlertDialog alert = builder.create();
		return alert;
	}
	
	/**
	 * This method create confirm dialog for useful on confirmation.
	 * 
	 * @param activity
	 * 			 the activity for getting resources, creating dialog
	 * @param title
	 * 			 the title text, if null setting confirm
	 * @param text
	 * 			 the confirm message text
	 * @param positivListener
	 *  		 the implemented interface for update user interface after click on positive button
	 * @return
	 * 			the confirm dialog
	 */
	public static Dialog createConfirmDialog(final Activity activity, final String title, final View view, 
			final DialogInterface.OnClickListener positivListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		if (title != null) {
			builder.setTitle(title);
		}
		
		DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.dismiss();
			}
		};

		builder.setView(view);
		builder.setPositiveButton(activity.getResources().getString(R.string.button_ok), positivListener);
		builder.setNegativeButton(activity.getResources().getString(R.string.button_cancel), negativeListener);

		return builder.create();
	}
	
	/**
	 * This method create confirm dialog for useful on confirmation.
	 * 
	 * @param activity
	 * 			 the activity for getting resources, creating dialog
	 * @param title
	 * 			 the title text, if null setting confirm
	 * @param text
	 * 			 the confirm message text
	 * @param positivListener
	 *  		 the implemented interface for update user interface after click on positive button
	 * @return
	 * 			the confirm dialog
	 */
	public static Dialog createConfirmDialog(final Activity activity, final String title, final View view, 
			final DialogInterface.OnClickListener positivListener,
			final DialogInterface.OnClickListener negativeListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		if (title != null) {
			builder.setTitle(title);
		}
		
		builder.setView(view);
		builder.setPositiveButton(activity.getResources().getString(R.string.button_ok), positivListener);
		builder.setNegativeButton(activity.getResources().getString(R.string.button_cancel), negativeListener);

		return builder.create();
	}
	
	/**
	 * This method create confirm dialog for useful on confirmation.
	 * 
	 * @param activity
	 * 			 the activity for getting resources, creating dialog
	 * @param title
	 * 			 the title text, if null setting confirm
	 * @param text
	 * 			 the confirm message text
	 * @param positivListener
	 *  		 the implemented interface for update user interface after click on positive button
	 * @return
	 * 			the confirm dialog
	 */
	public static Dialog createConfirmDialog(final Activity activity, final String title, final String text, 
			final int positiveId, final int negativeId,
			final DialogInterface.OnClickListener positivListener,
			final DialogInterface.OnClickListener negativeListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		if (title == null) {
			builder.setTitle(activity.getResources().getString(R.string.button_ok));
		} else {
			builder.setTitle(title);
		}
		
		builder.setMessage(text);
		builder.setPositiveButton(activity.getResources().getString(positiveId), positivListener);
		builder.setNegativeButton(activity.getResources().getString(negativeId), negativeListener);

		return builder.create();
	}
	
	/**
	 * This method create confirm dialog for useful on confirmation.
	 * 
	 * @param activity
	 * 			 the activity for getting resources, creating dialog
	 * @param text
	 * 			 the confirm message text
	 * @param positivListener
	 *  		 the implemented interface for update user interface after click on positive button
	 * @return
	 * 			the confirm dialog
	 */
	public static Dialog createConfirmDialog(final Activity activity, final String text, 
			final DialogInterface.OnClickListener positivListener) {
		DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.dismiss();
			}
		};
		return createConfirmDialog(activity, null, text, R.string.button_yes, R.string.button_no, positivListener, negativeListener);
	}

	/**
	 * This method create alert dialog for useful on show message.
	 * 
	 * @param activity
	 * 			 the activity for getting resources, creating dialog
	 * @param text
	 * 			 the message text
	 * @return
	 * 			the alert dialog
	 */
	public static Dialog createAlertDialog(final Activity activity, final String text) {
		return createAlertDialog(activity, null, text);
	}

	/**
	 * This method create alert dialog for useful on show message.
	 * 
	 * @param context
	 * 			 the context for getting resources, creating dialog
	 * @param title
	 * 			the title text
	 * @param text
	 * 			 the message text
	 * 
	 * @return
	 * 			the alert dialog
	 */
	public static Dialog createAlertDialog(final Context context, final String title, final String text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (title != null) {
			builder.setTitle(title);
		}
		builder.setMessage(text);
		builder.setPositiveButton(context.getResources().getString(R.string.button_ok), 
				new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(final DialogInterface d, final int w) {
				d.dismiss();
			}
		});
		return builder.create();
	}
	
	/**
	 * Show dialog error message.
	 * 
	 * @param context for getting resources
	 * @param resourceMessage the resources message
	 */
	public static void showCriticalErrorMessageDialog(final Context context, String title, Boolean quit, String resourceMessage, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (title == null) {
			builder.setTitle(context.getText(R.string.dialog_title_error));
		} else {
			builder.setTitle(title);
		}
		builder.setMessage(resourceMessage);
		String buttonTitle = null;
		if (quit) {
			buttonTitle = context.getResources().getString(R.string.button_close);
		} else {
			buttonTitle = context.getResources().getString(R.string.button_ok);
		}
		Builder dialog = builder.setPositiveButton(buttonTitle, listener);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent keyevent) {
				if (keyevent.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyevent.getKeyCode()) {
					case KeyEvent.KEYCODE_BACK:
						return true;
					case KeyEvent.KEYCODE_SEARCH:
						return true;
					default:
						break;
					}
				}
				return false;
			}
			
		});
		dialog.show();
	}

	/**
	 * Show dialog warning message.
	 * 
	 * @param context for getting resources
	 * @param resourceMessage the resources message
	 */
	public static void showWarningMessageDialog(final Context context, String resourceMessage, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getText(R.string.dialog_title_warning));
		builder.setMessage(resourceMessage);
		builder.setNegativeButton(context.getResources().getString(R.string.button_close), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		if (listener != null) {
			builder.setPositiveButton(context.getResources().getString(R.string.button_ok), 
				listener).show();
		}
	}
	
	/**
	 * Show dialog error message.
	 * 
	 * @param context for getting resources
	 * @param resourceMessage the resources message
	 */
	public static void showCriticalErrorMessageDialog(final Context context, Integer title, final Boolean quit, int resourceMessage) {
		String sTitle = null;
		if (title != null) {
			sTitle = context.getString(title);
		}
		
		showCriticalErrorMessageDialog(context, sTitle, quit, context.getString(resourceMessage), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(final DialogInterface d, final int w) {
				d.dismiss();
				ExceptionHandler.getInstance().clearHaveEx();
				if (quit) {
					if (context instanceof Activity) {
						((Activity) context).finish();
					}
				}
			}
		});
	}
}
