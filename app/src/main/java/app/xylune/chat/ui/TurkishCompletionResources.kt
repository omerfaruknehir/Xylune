package app.xylune.chat.ui

import app.xylune.chat.R

/** Additional resource-backed Xylune UI copy discovered by the Turkish coverage audit. */
internal fun xyluneTurkishCompletionResource(text: String): Int? = when (text) {
    "Attachment compatibility" -> R.string.tr_complete_attachment_compatibility
    "Prepare OCR" -> R.string.tr_complete_prepare_ocr
    "Zoom in" -> R.string.tr_complete_zoom_in
    "Zoom out" -> R.string.tr_complete_zoom_out
    "Chat configuration" -> R.string.tr_complete_chat_configuration
    "Advanced limits and prompt behavior for this conversation. Thinking, effort, tools, files, and Deep Research live beside the message box." -> R.string.tr_complete_advanced_chat_configuration
    "Compressed context active" -> R.string.tr_complete_compressed_context_active
    "Compress" -> R.string.tr_complete_compress
    "Clear summary" -> R.string.tr_complete_clear_summary
    "Conversations" -> R.string.tr_complete_conversations
    "Chat actions" -> R.string.tr_complete_chat_actions
    "Choose model" -> R.string.tr_complete_choose_model
    "New conversation" -> R.string.tr_complete_new_conversation
    "DETAILS" -> R.string.tr_complete_details
    "ERROR" -> R.string.tr_complete_error
    "Ask a question, attach a file, or choose Search and Tools beside the message box." -> R.string.tr_complete_empty_chat_hint
    "Collapse work details" -> R.string.tr_complete_collapse_work_details
    "Collapse step" -> R.string.tr_complete_collapse_step
    "INPUT" -> R.string.tr_complete_input
    "Code execution" -> R.string.tr_complete_code_execution
    "OUTPUT" -> R.string.tr_complete_output
    "script diagnostics" -> R.string.tr_complete_script_diagnostics
    "SOURCE" -> R.string.tr_complete_source_upper
    "Cancelled" -> R.string.tr_complete_cancelled
    "QUERY" -> R.string.tr_complete_query_upper
    "Planning" -> R.string.tr_complete_planning
    "Researching" -> R.string.tr_complete_researching
    "Blocked" -> R.string.tr_complete_blocked
    "Unreported" -> R.string.tr_complete_unreported
    "Attachments and tools" -> R.string.tr_complete_attachments_and_tools
    "Add direction…" -> R.string.tr_complete_add_direction
    "Choose one or more images" -> R.string.tr_complete_choose_images
    "Photos" -> R.string.tr_complete_photos
    "Camera" -> R.string.tr_complete_camera
    "Choose thinking level" -> R.string.tr_complete_choose_thinking_level
    "No deliberate reasoning where the model API allows it" -> R.string.tr_complete_thinking_off_description
    "Use the model's provider-managed thinking mode" -> R.string.tr_complete_thinking_on_description
    "Fastest available reasoning" -> R.string.tr_complete_thinking_minimal_description
    "Short reasoning with lower latency" -> R.string.tr_complete_thinking_low_description
    "Balanced reasoning" -> R.string.tr_complete_thinking_medium_description
    "Thorough reasoning" -> R.string.tr_complete_thinking_high_description
    "Extended reasoning for difficult agentic work" -> R.string.tr_complete_thinking_xhigh_description
    "Maximum supported reasoning effort" -> R.string.tr_complete_thinking_max_description
    "Balanced" -> R.string.tr_complete_balanced
    "Choose search mode" -> R.string.tr_complete_choose_search_mode
    "Choose chat tools" -> R.string.tr_complete_choose_chat_tools
    "Activity" -> R.string.tr_complete_activity
    "Choose a scoped Android folder, an OAuth app folder, WebDAV/Nextcloud, or an S3-compatible bucket prefix. Turp avoids account-wide cloud access." -> R.string.tr_complete_cloud_scope_explanation
    "Turp Google Drive OAuth setup" -> R.string.tr_complete_google_drive_oauth_setup
    "Open setup guide" -> R.string.tr_complete_open_setup_guide
    "Open provider setup guide" -> R.string.tr_complete_open_provider_setup_guide
    "Compile & use" -> R.string.tr_complete_compile_and_use
    "generated source" -> R.string.tr_complete_generated_source
    "View attempts" -> R.string.tr_complete_view_attempts
    "generated content errors" -> R.string.tr_complete_generated_content_errors
    "Images" -> R.string.tr_complete_images
    "Choose one or more reference images" -> R.string.tr_complete_choose_reference_images
    "Current generated image preview" -> R.string.tr_complete_generated_image_preview
    "Turp was made with full vibe coding: features and changes were primarily directed in natural language and implemented with AI-assisted coding tools. It may contain serious defects." -> R.string.tr_complete_vibe_coding_notice
    "The app is provided “AS IS”, without warranties. Use it at your own risk. To the maximum extent permitted by applicable law, the author and contributors are not responsible for data loss, device damage, account loss, charges, security incidents, or other consequences arising from its use, modification, or distribution. Review the source and keep backups before relying on it." -> R.string.tr_complete_as_is_notice
    "Choose a chat model" -> R.string.tr_complete_choose_chat_model
    "Choose an image model" -> R.string.tr_complete_choose_image_model
    "Clear a filter or try a model name, author, or capability." -> R.string.tr_complete_model_picker_no_match_hint
    "You can start chatting now. Appearance, local execution, backups, memory, and other optional features remain in clearly grouped Settings." -> R.string.tr_complete_setup_finished_hint
    "Blur source recording / extra draws" -> R.string.tr_complete_blur_source_recording
    "Collapse source" -> R.string.tr_complete_collapse_source
    "Checking installed packages and resolving dependencies…" -> R.string.tr_complete_resolving_packages
    "Approved automatically • installing now…" -> R.string.tr_complete_auto_approved_installing
    "Collapse package plan" -> R.string.tr_complete_collapse_package_plan
    "• Runtime health and per-chat Python packages" -> R.string.tr_complete_runtime_health_bullet
    "• Linux installation, packages, terminal, and removal" -> R.string.tr_complete_linux_management_bullet
    "• Test runs only; chat tool permissions are controlled per chat" -> R.string.tr_complete_test_runs_bullet
    "Clear run state" -> R.string.tr_complete_clear_run_state
    "Approved automatically • installing and verifying imports…" -> R.string.tr_complete_auto_approved_verifying
    "Checking the current Python environment…" -> R.string.tr_complete_checking_python_environment
    "Collapse plan" -> R.string.tr_complete_collapse_plan
    "Choose a rootless user-space distribution for broader third-party CLIs and libraries. Each distribution keeps its own packages, shares this chat's files at /workspace, and is a compatibility layer—not a security boundary." -> R.string.tr_complete_linux_distribution_explanation
    "• Requires a network download and app-private storage." -> R.string.tr_complete_linux_network_bullet
    "• Turp verifies the archive before extraction and exposes progress for every stage." -> R.string.tr_complete_linux_verify_bullet
    "• A failed or interrupted setup can be retried; /workspace chat files are not deleted." -> R.string.tr_complete_linux_retry_bullet
    "• No Android root access is used or requested." -> R.string.tr_complete_linux_no_root_bullet
    "Approved automatically • repairing package state and installing…" -> R.string.tr_complete_auto_approved_repairing
    "Collapse complete plan" -> R.string.tr_complete_collapse_complete_plan
    "Changing the launcher icon briefly restarts Turp after saving the open page, chat drafts and files, and current scroll positions. Android themed icons can still override app-selected colors." -> R.string.tr_complete_launcher_restart_explanation
    "Attributes slow frames to Android frame stages, Turp blur work, Compose recomposition pressure, allocations, and blocking GC. It adds some diagnostic overhead, so use it while reproducing an issue." -> R.string.tr_complete_profiler_explanation
    "Detailed mode shows Choreographer FPS, average/p95/p99 frame interval, jank against the current refresh budget, app CPU, PSS, Java heap, GPU duration when Android reports it, missed vsyncs per second, and total observed frames. Cause profiler ranks primary and secondary causes, reports confidence and severity, and shows the evidence used for attribution alongside FrameMetrics, blur, recomposition, allocation, and GC counters." -> R.string.tr_complete_detailed_metrics_explanation
    "Compact mode shows FPS, average frame time, and jank percentage." -> R.string.tr_complete_compact_metrics_explanation
    "Automatic checks are unavailable because this build has no embedded GitHub repository origin." -> R.string.tr_complete_no_repository_origin
    "Turp's natural green Material palette" -> R.string.tr_complete_natural_green_palette
    "Colors generated from your wallpaper on Android 12+" -> R.string.tr_complete_wallpaper_colors
    "Turp's versioned core prompt is built into the app and updates with Turp. Profiles can adjust tone or add preferences, but cannot replace the core capability, tool, research, date, privacy, or safety protocol." -> R.string.tr_complete_versioned_core_prompt
    "Choose a preset or connect a custom API endpoint." -> R.string.tr_complete_choose_provider_preset
    "Choose a backup" -> R.string.tr_complete_choose_backup
    "Choose where Turp should look. Every option is limited to app-only storage or a folder you explicitly select." -> R.string.tr_complete_choose_backup_source
    "Choose a backup folder" -> R.string.tr_complete_choose_backup_folder
    "• tap to check backups" -> R.string.tr_complete_tap_check_backups
    "Cloud & file backup" -> R.string.tr_complete_cloud_file_backup
    "Chats, branches, app configuration, organization, metadata, and optional attachments. API keys and OAuth sessions are deliberately excluded." -> R.string.tr_complete_backup_contents_explanation
    "Open shared Turp chat" -> R.string.tr_complete_open_shared_chat
    "Chats are imported as separate copies. Included app settings and organization are applied, but API keys, OAuth sessions, provider authorization headers, and cloud grants are never imported." -> R.string.tr_complete_import_safety_explanation
    else -> null
}
