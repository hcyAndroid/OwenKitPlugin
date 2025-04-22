package com.issyzone.syzds.internation

import com.issyzone.syzds.internation.SyzExcelUtils.labelIdAndRoidINdex
import com.issyzone.syzds.internation.SyzExcelUtils.labelModuleIndex
import com.issyzone.syzds.internation.SyzExcelUtils.labelTypeIndex
import com.issyzone.syzds.internation.SyzExcelUtils.readXMlFile


object MargeDataSta {
    private val totalData = mutableListOf<ITEM>()
    private val TAG = "合并数据:::"
    fun readALLXmlData(
        item: ITEM
    ): MutableList<ITEM> {
        val totalXMlData = mutableListOf<ITEM>()
        enumValues<SupportLanguage>().forEachIndexed { indexLanguage, language ->
            println("语言: 路径: ${language.valuePath}, Excel 名称: ${language.excelName}")
            val currentXmlHandler = readXMlFile(language.valuePath)
            val currentLanguage = language.excelName
            currentXmlHandler?.apply {
                val strXmlList = currentXmlHandler.stringResources
                val strArrayXMlList = currentXmlHandler.arrayResources
                strXmlList.forEachIndexed { index, stringResource ->
                    val findStr = totalXMlData.find {
                        it.valueList.get(labelIdAndRoidINdex).tagValue == stringResource.id && it.valueList.get(
                            labelTypeIndex
                        ).tagValue == "string"
                    }
                    if (findStr == null) {
                        //插入,以英文为标准
                        if (indexLanguage == 0) {
                            val item = SyzExcelUtils.deepCopy(item)
                            item.valueList[labelIdAndRoidINdex].tagValue = stringResource.id
                            item.valueList[labelTypeIndex].tagValue = "string"
                            item.valueList.find {
                                it.tagName == currentLanguage
                            }?.tagValue = stringResource.value
                            totalXMlData.add(item)
                        }
                    } else {
                        findStr.valueList[labelIdAndRoidINdex].tagValue = stringResource.id
                        findStr.valueList[labelTypeIndex].tagValue = "string"
                        findStr.valueList.find {
                            it.tagName == currentLanguage
                        }?.tagValue = stringResource.value
                    }
                }
                strArrayXMlList.forEach { (key, value) ->
                    value.forEach {
                        val id = "${key}$$${it.id}"
                        val arrXmlValue = it.value
                        val find = totalXMlData.find {
                            it.valueList.get(labelIdAndRoidINdex).tagValue == id && it.valueList.get(
                                labelTypeIndex
                            ).tagValue == "string-array"
                        }
                        if (find == null) {
                            if (indexLanguage == 0) {
                                val newItem = SyzExcelUtils.deepCopy(item)
                                newItem.valueList.get(labelIdAndRoidINdex).tagValue = id
                                newItem.valueList.get(labelTypeIndex).tagValue = "string-array"
                                newItem.valueList.find {
                                    it.tagName == currentLanguage
                                }?.tagValue = arrXmlValue
                                totalXMlData.add(newItem)
                            }

                        } else {
                            find.valueList[labelIdAndRoidINdex].tagValue = id
                            find.valueList[labelTypeIndex].tagValue = "string-array"
                            find.valueList.find {
                                it.tagName == currentLanguage
                            }?.tagValue = arrXmlValue
                        }
                    }
                }
            }
        }
        totalXMlData.forEach {
            println("读取本地XML数据${totalXMlData.size}===${it}\n")
        }
        return totalXMlData
    }

    fun readALLXmlData2(
        item: ITEM
    ): MutableList<MutableList<ITEM>> {
      //
        val list= mutableListOf<MutableList<ITEM>>()
        //获取多个模块的数据
        SupportModule.values().forEachIndexed { index, supportModule ->
            val totalXMlData = mutableListOf<ITEM>()
            val dl= mutableListOf(SupportLanguage.English) //以英文为标准
            SupportLanguage.values().forEachIndexed { index, supprotLanguage ->
                if (supprotLanguage != SupportLanguage.English) {
                    dl.add(supprotLanguage)
                }
            }
            dl.forEachIndexed { indexLanguage, language ->
                val xmlPath = "${supportModule.moduleTag}/${language.valuePath.substringAfter('/')}"
                println("语言: 路径: ${xmlPath}, 模块: ${supportModule.moduleTag}")
                val currentXmlHandler = readXMlFile(xmlPath)
                val currentLanguage = language.excelName
                currentXmlHandler?.apply {
                    val strXmlList = currentXmlHandler.stringResources
                    val strArrayXMlList = currentXmlHandler.arrayResources
                    strXmlList.forEachIndexed { index, stringResource ->
                        val findStr = totalXMlData.find {
                            it.valueList.get(labelIdAndRoidINdex).tagValue == stringResource.id && it.valueList.get(
                                labelTypeIndex
                            ).tagValue == "string"
                        }
                        if (findStr == null) {
                            //插入,以英文为标准
                            if (indexLanguage == 0) {
                                val item = SyzExcelUtils.deepCopy(item)
                                item.valueList[labelIdAndRoidINdex].tagValue = stringResource.id
                                item.valueList[labelTypeIndex].tagValue = "string"
                                item.valueList[SyzExcelUtils.labelModuleIndex].tagValue = supportModule.moduleTag
                                item.valueList.find {
                                    it.tagName == currentLanguage
                                }?.tagValue = stringResource.value
                                totalXMlData.add(item)
                            }
                        } else {
                            findStr.valueList[labelIdAndRoidINdex].tagValue = stringResource.id
                            findStr.valueList[labelTypeIndex].tagValue = "string"
                            findStr.valueList[SyzExcelUtils.labelModuleIndex].tagValue = supportModule.moduleTag
                            findStr.valueList.find {
                                it.tagName == currentLanguage
                            }?.tagValue = stringResource.value
                        }
                    }
                    strArrayXMlList.forEach { (key, value) ->
                        value.forEach {
                            val id = "${key}$$${it.id}"
                            val arrXmlValue = it.value
                            val find = totalXMlData.find {
                                it.valueList.get(labelIdAndRoidINdex).tagValue == id && it.valueList.get(
                                    labelTypeIndex
                                ).tagValue == "string-array"
                            }
                            if (find == null) {
                                if (indexLanguage == 0) {
                                    val newItem = SyzExcelUtils.deepCopy(item)
                                    newItem.valueList.get(labelIdAndRoidINdex).tagValue = id
                                    newItem.valueList.get(labelTypeIndex).tagValue = "string-array"
                                    newItem.valueList[labelModuleIndex].tagValue = supportModule.moduleTag
                                    newItem.valueList.find {
                                        it.tagName == currentLanguage
                                    }?.tagValue = arrXmlValue
                                    totalXMlData.add(newItem)
                                }

                            } else {
                                find.valueList[labelIdAndRoidINdex].tagValue = id
                                find.valueList[labelTypeIndex].tagValue = "string-array"
                                find.valueList[labelModuleIndex].tagValue = supportModule.moduleTag
                                find.valueList.find {
                                    it.tagName == currentLanguage
                                }?.tagValue = arrXmlValue
                            }
                        }
                    }
                }
            }
            list.add(totalXMlData)
        }
        list.forEachIndexed { index, items ->
            if (index==0){
                println("读取${SupportModule.values().get(index).moduleTag}模块的XML数据${items.size}\n")
                items.forEach {item ->
                    println("读取${SupportModule.values().get(index).moduleTag}模块的XML数据${items.size}===${item}\n")
                }
            }

        }
        return list
    }

    fun margeExcelData(
        excelData: MutableList<ITEM>,
        strXmlData: MutableList<ITEM>,
    ): MutableList<ITEM> {
        val xmlcount = strXmlData.size
        val excelcount = excelData.size
        excelData.forEachIndexed { index, excelItem ->
            val find = strXmlData.find {
                it.valueList.get(labelIdAndRoidINdex).tagValue == excelItem.valueList.get(
                    labelIdAndRoidINdex
                ).tagValue && it.valueList.get(labelTypeIndex).tagValue == excelItem.valueList.get(
                    labelTypeIndex
                ).tagValue
            }
            if (find == null) {
                strXmlData.add(excelItem)
            } else {
                excelItem.valueList.forEach { ex ->
                    find.valueList.find {
                        it.tagName == ex.tagName
                    }?.tagValue = ex.tagValue
                }
            }
        }
//        strXmlData.forEach {
//            println("合并表格之后XML数据${strXmlData.size}=合并前xml${xmlcount}表格${excelcount}====${it}\n")
//        }
        return strXmlData
    }
}