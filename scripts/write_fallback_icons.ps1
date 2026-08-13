$out = "E:\notesup\app\src\main\res\drawable"
$names = @(
  "arrow_back","search","account_circle","grid_view","view_agenda","keep",
  "add","keyboard_arrow_up","notes","checklist","draw","image","photo_camera",
  "photo_library","more_vert","share","description","picture_as_pdf","lock",
  "lock_open","delete","undo","redo","layers","inbox","settings","sync",
  "cloud_off","error_outline","check","close","format_bold","format_italic",
  "format_underlined","format_strikethrough","code","link","title",
  "format_list_bulleted","format_list_numbered","horizontal_rule","edit",
  "highlight","ink_eraser","line_weight","palette","drive_file_move",
  "format_color_fill","select_all","drag_indicator","info","logout","login",
  "key","crop_free","schedule","format_quote","table_chart","terminal",
  "opacity","open_in_full","content_copy"
)
# Official-ish 24dp Material paths (simplified Rounded)
$paths = @{
  add = "M19,13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"
  arrow_back = "M20,11H7.83l5.59,-5.59L12,4l-8,8 8,8 1.41,-1.41L7.83,13H20v-2z"
  search = "M15.5,14h-0.79l-0.28,-0.27A6.47,6.47 0,0 0,16 9.5 6.5,6.5 0,1 0,9.5 16c1.61,0 3.09,-0.59 4.23,-1.57l0.27,0.28v0.79L20,21.49 21.49,20 15.5,14zM9.5,14C7.01,14 5,11.99 5,9.5S7.01,5 9.5,5 14,7.01 14,9.5 11.99,14 9.5,14z"
  check = "M9,16.17L4.83,12l-1.42,1.41L9,19 21,7l-1.41,-1.41z"
  close = "M19,6.41L17.59,5 12,10.59 6.41,5 5,6.41 10.59,12 5,17.59 6.41,19 12,13.41 17.59,19 19,17.59 13.41,12z"
  delete = "M6,19c0,1.1 0.9,2 2,2h8c1.1,0 2,-0.9 2,-2V7H6v12zM19,4h-3.5l-1,-1h-5l-1,1H5v2h14V4z"
  more_vert = "M12,8c1.1,0 2,-0.9 2,-2s-0.9,-2 -2,-2 -2,0.9 -2,2 0.9,2 2,2zM12,10c-1.1,0 -2,0.9 -2,2s0.9,2 2,2 2,-0.9 2,-2 -0.9,-2 -2,-2zM12,16c-1.1,0 -2,0.9 -2,2s0.9,2 2,2 2,-0.9 2,-2 -0.9,-2 -2,-2z"
  settings = "M19.14,12.94c0.04,-0.31 0.06,-0.63 0.06,-0.94 0,-0.31 -0.02,-0.63 -0.06,-0.94l2.03,-1.58c0.18,-0.14 0.23,-0.41 0.12,-0.61l-1.92,-3.32c-0.12,-0.22 -0.37,-0.29 -0.59,-0.22l-2.39,0.96c-0.5,-0.38 -1.03,-0.7 -1.62,-0.94L14.4,2.81c-0.04,-0.24 -0.24,-0.41 -0.48,-0.41h-3.84c-0.24,0 -0.43,0.17 -0.47,0.41L9.25,5.35C8.66,5.59 8.12,5.92 7.63,6.29L5.24,5.33c-0.22,-0.08 -0.47,0 -0.59,0.22L2.74,8.87C2.62,9.08 2.66,9.34 2.86,9.48l2.03,1.58C4.84,11.36 4.8,11.69 4.8,12s0.02,0.63 0.06,0.94l-2.03,1.58c-0.18,0.14 -0.23,0.41 -0.12,0.61l1.92,3.32c0.12,0.22 0.37,0.29 0.59,0.22l2.39,-0.96c0.5,0.38 1.03,0.7 1.62,0.94l0.36,2.54c0.05,0.24 0.24,0.41 0.48,0.41h3.84c0.24,0 0.44,-0.17 0.47,-0.41l0.36,-2.54c0.59,-0.24 1.13,-0.56 1.62,-0.94l2.39,0.96c0.22,0.08 0.47,0 0.59,-0.22l1.92,-3.32c0.12,-0.22 0.07,-0.47 -0.12,-0.61L19.14,12.94zM12,15.6c-1.98,0 -3.6,-1.62 -3.6,-3.6s1.62,-3.6 3.6,-3.6 3.6,1.62 3.6,3.6 -1.62,3.6 -3.6,3.6z"
}
foreach ($n in $names) {
  $dest = "$out\ic_$n.xml"
  if (Test-Path $dest) {
    $len = (Get-Item $dest).Length
    if ($len -gt 80) { continue }
  }
  $p = if ($paths.ContainsKey($n)) { $paths[$n] } else { $paths["add"] }
  @"
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?android:attr/textColorPrimary">
    <path android:fillColor="@android:color/white" android:pathData="$p"/>
</vector>
"@ | Set-Content -Path $dest -Encoding utf8
}
Write-Output "icons ready $((Get-ChildItem $out\ic_*.xml).Count)"
