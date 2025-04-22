package com.issyzone.syzds.internation
//支持的语言
enum class SupportLanguage(val valuePath:String,val excelName:String) {
    CHINESE("app/src/main/res/values-zh-rCN/strings.xml","中文"),
    English("app/src/main/res/values/strings.xml","英文"),
    Spanish("app/src/main/res/values-es-rES/strings.xml","西班牙语"),
    JAPAN("app/src/main/res/values-ja-rJP/strings.xml","日语"),
    French("app/src/main/res/values-fr-rFR/strings.xml","法语"),
    German("app/src/main/res/values-de-rCH/strings.xml","德语"),
    Italian("app/src/main/res/values-it-rCH/strings.xml","意大利语"),
    //葡萄牙语
    Portuguese("app/src/main/res/values-pt-rPT/strings.xml","巴西葡萄牙语")
    //荷兰
   // Dutch("app/src/main/res/values-nl-rNL/strings.xml","荷兰语"),
}
//指出的模块
enum class SupportModule(val moduleTag:String){
    APP("app"),EDITOR("editor")
}