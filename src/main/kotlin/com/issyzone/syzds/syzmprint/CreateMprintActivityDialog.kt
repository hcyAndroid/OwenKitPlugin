package com.issyzone.syzds.syzmprint

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import java.awt.GridLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import com.intellij.openapi.project.Project
import com.intellij.openapi.module.Module
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.dom.manifest.Manifest

class CreateMprintActivityDialog(var project: Project?, val e: AnActionEvent) : DialogWrapper(project) {
    private lateinit var activityNameField: JTextField
    private lateinit var generateLayoutCheckbox: JCheckBox
    private lateinit var addToManifestCheckbox: JCheckBox

    //  private lateinit var templateTypeCombo: JComboBox<String>
    private val logger = Logger.getInstance(CreateMprintActivityDialog::class.java)

    init {
        title = "Create New Activity"
        init()
    }

    private  var currentModule: Module?=null;
    private  var currentPackName:String?=null;

    fun getModuleFromEvent(e: AnActionEvent): Module? {
        val project = e.getData(CommonDataKeys.PROJECT) ?: return null
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return null

        return ModuleUtil.findModuleForFile(file, project) ?: run {
            Messages.showErrorDialog("Error: Could not determine module from context", "Error")
            null
        }
    }

    fun getModulePackageName(module: Module): String {
        val androidFacet = AndroidFacet.getInstance(module)
        return androidFacet?.let {
            Manifest.getMainManifest(it)?.`package`?.stringValue
        } ?: "未找到包名"
    }

    override fun createCenterPanel(): JComponent? {
        val panel = JPanel(GridLayout(0, 1))

        // Activity 名称输入
        currentModule = getModuleFromEvent(e)
        panel.add(JLabel("Module Name:${currentModule?.name ?: "未找到模块"}"))
        if (currentModule!=null){
            currentPackName=getModulePackageName(currentModule!!)
            panel.add(JLabel("Package Name:${currentPackName}}"))
        }
        panel.add(JLabel("Activity Name:"))
        activityNameField = JTextField()
        panel.add(activityNameField)
        // 选项
        generateLayoutCheckbox = JCheckBox("Generate layout file", true)
        panel.add(generateLayoutCheckbox)

        addToManifestCheckbox = JCheckBox("Add to AndroidManifest.xml", true)
        panel.add(addToManifestCheckbox)

        return panel
    }

    override fun doOKAction() {
        logger.info("开始创建")
        val activityName = activityNameField.text.trim()
        if (activityName.isEmpty()) {
            Messages.showErrorDialog("Please enter activity name", "Error")
            return
        }
        if (currentModule==null||currentPackName==null){
            Messages.showErrorDialog("Error: Could not determine module or package name", "Error")
            return
        }

        //  val templateType = templateTypeCombo.selectedItem as String
        val generateLayout = generateLayoutCheckbox.isSelected
        val addToManifest = addToManifestCheckbox.isSelected
        logger.info("开始创建= $activityName= $generateLayout $addToManifest")
        // 执行创建逻辑
        MprintActivityCreator(activityName, generateLayout, addToManifest,currentModule!!,currentPackName!!).create(project)
        super.doOKAction()
    }
}