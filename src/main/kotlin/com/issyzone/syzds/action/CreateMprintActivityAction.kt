package com.issyzone.syzds.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.module.ModuleUtil
import com.issyzone.syzds.syzmprint.CreateMprintActivityDialog
import org.jetbrains.android.facet.AndroidFacet
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.Messages


class CreateMprintActivityAction : AnAction() {
    // 菜单项名称和描述（会显示在右键菜单中）
    private val logger = Logger.getInstance(CreateMprintActivityAction::class.java)

    init {
        templatePresentation.text = "New MPrint Activity"
        templatePresentation.description = "Create a new MPrint Activity with template"

    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        // 显示创建对话框
        logger.info("弹出创建的对话框")
        CreateMprintActivityDialog(project,e).show()
    }

    // 只在Android模块中显示该Action
    override fun update(e: AnActionEvent) {
        val project = e.project
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)

        val isAndroidModule =
            project != null && file != null && ModuleUtil.findModuleForFile(file, project)?.let { module ->
                AndroidFacet.getInstance(module) != null
            } ?: false
        e.presentation.isEnabledAndVisible = true
    }
}