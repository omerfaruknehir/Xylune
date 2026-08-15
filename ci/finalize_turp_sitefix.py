from pathlib import Path
import re


def replace_once(path: str, old: str, new: str) -> None:
    p = Path(path)
    text = p.read_text()
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{path}: expected exactly one match, found {count}: {old[:160]!r}")
    p.write_text(text.replace(old, new, 1))


# Finish the website's animated logo path. The stable xylune storage/scheme key stays
# intentionally unchanged; the visible artwork and default brand palette become Turp.
logo_path = Path("docs/assets/js/logo-motion.js")
logo = logo_path.read_text()
logo = logo.replace(
    "backgroundStart: '#083a2c',\n      backgroundEnd: '#0c684f',\n      markStart: '#86dfb8',\n      markEnd: '#ddfbea',\n      leaf: '#f4c761',\n      secondStroke: '#f1fff7',",
    "backgroundStart: '#fff0d7',\n      backgroundEnd: '#fde1bd',\n      markStart: '#78bf43',\n      markEnd: '#28722e',\n      leaf: '#ef2e52',\n      secondStroke: '#f5a0b0',",
    1,
)
radish_logo_function = r'''  function logoDataUrl(palette) {
    const svg = `<svg width="512" height="512" viewBox="0 0 1536 1536" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <linearGradient id="bg" x1="220" y1="120" x2="1340" y2="1420" gradientUnits="userSpaceOnUse">
      <stop stop-color="${palette.backgroundStart}"/>
      <stop offset="1" stop-color="${palette.backgroundEnd}"/>
    </linearGradient>
    <linearGradient id="leaf" x1="720" y1="220" x2="860" y2="680" gradientUnits="userSpaceOnUse">
      <stop stop-color="${palette.markStart}"/>
      <stop offset="1" stop-color="${palette.markEnd}"/>
    </linearGradient>
  </defs>
  <rect width="1536" height="1536" rx="350" fill="url(#bg)"/>
  <path d="M774 649C784 604 779 559 748 520C715 480 662 455 643 408C621 354 629 289 650 242C664 210 684 206 708 216C754 236 805 280 840 337C866 379 874 430 856 489C842 540 818 595 795 648Z" fill="url(#leaf)"/>
  <path d="M827 674C874 624 927 570 1000 519C1060 477 1112 454 1155 466C1210 481 1260 515 1282 548C1304 579 1286 617 1260 649C1215 705 1168 732 1115 726C1078 722 1049 701 1015 684C973 663 935 656 900 670C870 682 846 690 827 674Z" fill="url(#leaf)"/>
  <path d="M817 661C801 597 802 523 805 447C809 372 840 315 890 264C936 216 982 178 1017 176C1057 174 1090 198 1111 237C1134 281 1141 342 1137 393C1133 444 1115 489 1085 518C1054 547 1012 563 969 576C912 594 858 616 817 661Z" fill="url(#leaf)"/>
  <path d="M734 681C686 657 633 648 586 654C519 663 463 699 428 747C399 786 389 828 400 875C406 903 421 936 440 970C458 1004 469 1035 469 1070C470 1122 449 1173 414 1219C390 1250 365 1278 378 1289C386 1296 414 1267 449 1239C491 1205 532 1181 574 1169C618 1157 669 1167 718 1161C771 1154 820 1131 858 1091C895 1053 918 1005 923 953C930 891 916 836 884 791C846 738 790 705 734 681Z" fill="${palette.leaf}"/>
  <path d="M440 989C471 1005 500 1024 531 1048C570 1078 601 1114 631 1158C600 1159 570 1166 541 1178C499 1195 462 1223 428 1253C402 1276 382 1295 376 1290C369 1284 393 1253 415 1225C451 1179 470 1128 468 1072C468 1037 457 1006 440 989Z" fill="${palette.secondStroke}"/>
</svg>`;
    return `data:image/svg+xml,${encodeURIComponent(svg)}`;
  }
'''
logo, count = re.subn(
    r"  function logoDataUrl\(palette\) \{.*?\n  \}\n\n  function installDialogLogoPreview",
    radish_logo_function + "\n  function installDialogLogoPreview",
    logo,
    count=1,
    flags=re.S,
)
if count != 1:
    raise SystemExit(f"logo-motion.js: logoDataUrl replacement count={count}")
logo_path.write_text(logo)

# The current site tests previously asserted the old Xylune line-art implementation.
legal_test = "app/src/test/java/app/xylune/chat/ui/LegalWebsiteIntegrationTest.kt"
replace_once(
    legal_test,
    '        assertTrue(!site.contains("<linearGradient id=\\"leaf\\""))',
    '        assertTrue(site.contains("<linearGradient id=\\"leaf\\""))',
)
replace_once(
    legal_test,
    '        assertTrue(home.contains("class=\\"home-hero__backdrop\\""))',
    '        assertTrue(home.contains("home-hero__backdrop--turp"))',
)

switch_test = "app/src/test/java/app/xylune/chat/ui/WebsiteSwitchPopupRegressionTest.kt"
replace_once(
    switch_test,
    '        assertTrue(logoMotion.contains("function animateTo("))',
    '        assertTrue(logoMotion.contains("function animateTo("))\n        assertTrue(logoMotion.contains("M734 681"))\n        assertTrue(!logoMotion.contains("M33.549193"))',
)

# Preserve legacy paths for history/caches, but make anything still referencing them render
# the Turp artwork rather than the retired Xylune mark.
radish = Path("branding/turp-radish.svg").read_text()
for path in (Path("branding/xylune-logo.svg"), Path("licenses/icons/xylune.svg")):
    if path.exists():
        path.write_text(radish)

assert "M734 681" in logo_path.read_text()
assert "M33.549193" not in logo_path.read_text()
assert "home-hero__backdrop--turp" in Path(legal_test).read_text()
assert "M734 681" in Path(switch_test).read_text()
assert Path("branding/xylune-logo.svg").read_text() == radish
print("Turp website/logo migration completed")
