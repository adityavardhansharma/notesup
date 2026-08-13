package com.notesup.app.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.notesup.app.R

object NotesupIcons {
    val Back = R.drawable.ic_arrow_back
    val Search = R.drawable.ic_search
    val Account = R.drawable.ic_account_circle
    val Grid = R.drawable.ic_grid_view
    val List = R.drawable.ic_view_agenda
    val Pin = R.drawable.ic_keep
    val Add = R.drawable.ic_add
    val Split = R.drawable.ic_keyboard_arrow_up
    val Note = R.drawable.ic_notes
    val Checklist = R.drawable.ic_checklist
    val Draw = R.drawable.ic_draw
    val Image = R.drawable.ic_image
    val Camera = R.drawable.ic_photo_camera
    val Gallery = R.drawable.ic_photo_library
    val More = R.drawable.ic_more_vert
    val Share = R.drawable.ic_share
    val Md = R.drawable.ic_description
    val Pdf = R.drawable.ic_picture_as_pdf
    val Lock = R.drawable.ic_lock
    val Unlock = R.drawable.ic_lock_open
    val Delete = R.drawable.ic_delete
    val Undo = R.drawable.ic_undo
    val Redo = R.drawable.ic_redo
    val Project = R.drawable.ic_layers
    val Inbox = R.drawable.ic_inbox
    val Settings = R.drawable.ic_settings
    val Sync = R.drawable.ic_sync
    val Offline = R.drawable.ic_cloud_off
    val Check = R.drawable.ic_check
    val Close = R.drawable.ic_close
    val Bold = R.drawable.ic_format_bold
    val Italic = R.drawable.ic_format_italic
    val Underline = R.drawable.ic_format_underlined
    val Strike = R.drawable.ic_format_strikethrough
    val Code = R.drawable.ic_code
    val Link = R.drawable.ic_link
    val Heading = R.drawable.ic_title
    val Bullet = R.drawable.ic_format_list_bulleted
    val Number = R.drawable.ic_format_list_numbered
    val Divider = R.drawable.ic_horizontal_rule
    val Pen = R.drawable.ic_edit
    val Highlighter = R.drawable.ic_highlight
    val Eraser = R.drawable.ic_ink_eraser
    val Width = R.drawable.ic_line_weight
    val Palette = R.drawable.ic_palette
    val Move = R.drawable.ic_drive_file_move
    val Tint = R.drawable.ic_format_color_fill
    val Info = R.drawable.ic_info
    val SignOut = R.drawable.ic_logout
    val SignIn = R.drawable.ic_login
    val Passkey = R.drawable.ic_key
    val Google = R.drawable.ic_google
    val Capture = R.drawable.ic_crop_free
    val Recent = R.drawable.ic_schedule
    val Quote = R.drawable.ic_format_quote
    val Table = R.drawable.ic_table_chart
    val Terminal = R.drawable.ic_terminal
    val Copy = R.drawable.ic_content_copy
    val Expand = R.drawable.ic_open_in_full
    val Drag = R.drawable.ic_drag_indicator
}

@Composable
fun NuIcon(
    @DrawableRes id: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    Icon(painterResource(id), contentDescription, modifier, tint)
}
