$ErrorActionPreference = "Continue"
$base = "https://raw.githubusercontent.com/google/material-design-icons/master/symbols/android"
$out = "E:\notesup\app\src\main\res\drawable"
New-Item -ItemType Directory -Force -Path $out | Out-Null

$icons = @(
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

function Write-Fallback($name) {
  @"
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path android:fillColor="#FF000000" android:pathData="M12,2A10,10 0 1,0 22,12A10,10 0 0,0 12,2zM11,6h2v8h-2zM11,16h2v2h-2z"/>
</vector>
"@ | Set-Content -Path "$out\ic_$name.xml" -Encoding utf8
}

foreach ($name in $icons) {
  $urls = @(
    "$base/$name/materialsymbolsrounded/${name}_24px.xml",
    "$base/$name/materialsymbolsrounded/${name}_24px_0.xml"
  )
  $ok = $false
  foreach ($u in $urls) {
    try {
      $resp = Invoke-WebRequest -Uri $u -UseBasicParsing -TimeoutSec 20
      if ($resp.StatusCode -eq 200 -and $resp.Content) {
        $xml = $resp.Content
        # ensure fill is black so tint works
        $dest = "$out\ic_$name.xml"
        $xml | Set-Content -Path $dest -Encoding utf8
        Write-Output "OK $name"
        $ok = $true
        break
      }
    } catch {}
  }
  if (-not $ok) {
    Write-Fallback $name
    Write-Output "FALLBACK $name"
  }
}
