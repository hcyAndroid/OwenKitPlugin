package com.issyzone.syzds.syzmprint
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.dom.manifest.Manifest
import java.io.File
import com.android.tools.idea.gradle.project.model.AndroidModuleModel
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.issyzone.syzds.action.CreateMprintActivityAction

class MprintActivityCreator(
    private val activityName: String,
    private val generateLayout: Boolean,
    private val addToManifest: Boolean,
    private val currentModule: Module,
    private val currentPackageName: String,
){
    private val logger = Logger.getInstance(MprintActivityCreator::class.java)


    fun getModuleInfo(project: Project, file: VirtualFile): String {
        // 获取模块
        val module: Module? = ModuleUtil.findModuleForFile(file, project)
        if (module == null) {
            return "未找到模块"
        }

        // 获取模块名
        val moduleName = module.name

        // 获取模块的包名
        val androidFacet = AndroidFacet.getInstance(module)
        val packageName = androidFacet?.let {
            Manifest.getMainManifest(it)?.`package`?.stringValue
        } ?: "未找到包名"

        return "模块名: $moduleName, 包名: $packageName"
    }
    fun create(project: Project?) {
        project ?: return
        createActivityFile(currentModule, currentPackageName)

        // 可选：添加到Manifest

    }


    private fun createActivityFile(module: Module, packageName: String) {
        val className = "${activityName}Activity"
        val layoutName = "activity_${activityName.lowercase()}"
        val bindingName = "${activityName}Binding"

        // 1. 准备模板代码
        val template = """
        |package $packageName
        
        |import android.os.Bundle
        |import android.view.View
        |import androidx.activity.viewModels
        |import ${packageName}.R
        |import ${packageName}.base.BaseMPrintActivity
        |import ${packageName}.databinding.${bindingName}
        |import ${packageName}.model.viewmodel.${activityName}Model
        
        |class $className : BaseMPrintActivity<${activityName}Model>() {
        |
        |    private val binding: ${bindingName} by lazy {
        |        ${bindingName}.inflate(layoutInflater)
        |    }
        |
        |    override val viewModel: ${activityName}Model by viewModels()
        |
        |    override fun getLayoutResId(): Int = R.layout.${layoutName}
        |
        |    override fun getLayoutRoot(): View = binding.root
        |
        |    override fun providerVMClass(): Class<${activityName}Model> = ${activityName}Model::class.java
        |
        |    override fun initView() {
        |        with(binding) {
        |            // 初始化UI组件
        |        }
        |    }
        |
        |    override fun initData() {
        |        // 初始化数据
        |    }
        |}
    """.trimMargin()

        // 2. 确定文件路径
        val packagePath = packageName.replace(".", "/")
        val directory = "${module.moduleFile?.parent?.path}/src/main/java/$packagePath/view/activity"

        // 3. 创建目录（如果不存在）
        File(directory).mkdirs()

        // 4. 写入文件
        val file = File("$directory/$className.kt")
        file.writeText(template)

        // 5. 如果勾选了生成布局
        if (generateLayout) {
            createLayoutFile(module, layoutName)
        }
    }

    private fun createLayoutFile(module: Module, layoutName: String) {
        val layoutDir = "${module.moduleFile?.parent?.path}/src/main/res/layout"
        File(layoutDir).mkdirs()

        val layoutTemplate = """
        |<?xml version="1.0" encoding="utf-8"?>
        |<androidx.constraintlayout.widget.ConstraintLayout 
        |    xmlns:android="http://schemas.android.com/apk/res/android"
        |    xmlns:app="http://schemas.android.com/apk/res-auto"
        |    android:layout_width="match_parent"
        |    android:layout_height="match_parent">
        |
        |    <!-- 默认添加一个TextView -->
        |    <TextView
        |        android:id="@+id/tvTitle"
        |        android:layout_width="wrap_content"
        |        android:layout_height="wrap_content"
        |        android:text="Hello ${activityName}!"
        |        app:layout_constraintBottom_toBottomOf="parent"
        |        app:layout_constraintLeft_toLeftOf="parent"
        |        app:layout_constraintRight_toRightOf="parent"
        |        app:layout_constraintTop_toTopOf="parent" />
        |
        |</androidx.constraintlayout.widget.ConstraintLayout>
    """.trimMargin()

        File("$layoutDir/$layoutName.xml").writeText(layoutTemplate)
    }



    fun getMainPackage(module: Module): String? {
        val androidFacet = AndroidFacet.getInstance(module) ?: return null
        return Manifest.getMainManifest(androidFacet)?.`package`?.stringValue
    }


    fun getApplicationId(module: Module): String? {
        return AndroidModuleModel.get(module)?.getApplicationId()
    }
}