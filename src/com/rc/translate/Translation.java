package com.rc.translate;

public class Translation {
    private static final String APP_ID = "20180531000169604";
    private static final String SECURITY_KEY = "O7AEIv9qL4KiSJ0JFTe1";

    //message为要翻译的句子，from为源语言，to为目的语言
    public String get_translation(String message, String from , String to){
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        String result = api.getTransResult(message,from,to);
        String[] translations = result.split("\"");
        return translations[5];
    }
}
