package com.sillyv.garbagecan.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by Vasili on 9/20/2017.
 */

public class RxDialog {

    private String title;
    private String postitiveMessage;
    private String negativeMessage;
    private Context context;
    private String message;
    private Integer numericTitle;
    private Integer numericPositiveMessage;
    private Integer numericNegativeMessage;
    private Integer numericMessage;

    public RxDialog(Context context) {
        this.context = context;
    }

    public Maybe<Boolean> show() {
        return Maybe.create(singleSubscriber -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            if (title != null) {
                builder.setTitle(title);
            } else if (numericTitle != null) {
                builder.setTitle(numericTitle);
            }


            if (message != null) {
                builder.setMessage(message);
            } else if (numericMessage != null) {
                builder.setMessage(numericMessage);
            }

            if (postitiveMessage != null || numericPositiveMessage != null) {
                DialogInterface.OnClickListener onClickListener = (dialogInterface, i) -> {
                    singleSubscriber.onSuccess(true);
                };
                if (postitiveMessage != null) {
                    builder.setPositiveButton(postitiveMessage, onClickListener);
                } else {
                    builder.setPositiveButton(numericPositiveMessage, onClickListener);
                }
            }

            if (negativeMessage != null || numericNegativeMessage != null) {
                DialogInterface.OnClickListener onClickListener = (dialogInterface, i) -> {
                    singleSubscriber.onSuccess(false);
                };
                if (negativeMessage != null) {
                    builder.setNegativeButton(negativeMessage, onClickListener);
                } else {
                    builder.setNegativeButton(numericNegativeMessage, onClickListener);
                }
            }

            builder.setOnDismissListener(dialogInterface -> {
                singleSubscriber.onComplete();
            });

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }


    private RxDialog(Builder builder) {
        title = builder.title;
        postitiveMessage = builder.postitiveMessage;
        negativeMessage = builder.negativeMessage;
        message = builder.message;
        numericTitle = builder.numericTitle;
        numericPositiveMessage = builder.numericPositiveMessage;
        numericNegativeMessage = builder.numericNegativeMessage;
        numericMessage = builder.numericMessage;


        context = builder.context;
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    public static final class Builder {
        private String title;
        private String postitiveMessage;
        private String negativeMessage;
        private String message;

        private Integer numericTitle;
        private Integer numericPositiveMessage;
        private Integer numericNegativeMessage;
        private Integer numericMessage;

        private Context context;

        private Builder(Context context) {
            this.context = context;
        }

        public Builder withMessage(int message) {
            this.numericMessage = message;
            return this;
        }

        public Builder withTitle(int title) {
            this.numericTitle = title;
            return this;
        }

        public Builder withPositiveMessage(int positiveMessage) {
            this.numericNegativeMessage = positiveMessage;
            return this;
        }

        public Builder withNegativeMessage(int negativeMessage) {
            this.numericNegativeMessage = negativeMessage;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withPositiveMessage(String positiveMessage) {
            this.postitiveMessage = positiveMessage;
            return this;
        }

        public Builder withNegativeMessage(String negativeMessage) {
            this.negativeMessage = negativeMessage;
            return this;
        }

        public RxDialog build() {
            return new RxDialog(this);
        }
    }
}
