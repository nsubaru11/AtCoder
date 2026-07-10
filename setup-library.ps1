param()

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$librarySrc = Join-Path $root "library\src"
$ideaDir = Join-Path $root ".idea"
$modulesPath = Join-Path $ideaDir "modules.xml"
$moduleName = "competitive-programming-library"
$modulePath = Join-Path $root "$moduleName.iml"
$legacyModulePath = Join-Path $ideaDir "$moduleName.iml"
$templateSource = Join-Path $root "template\AtCoderLibrarySolution.java.template"
$templateDir = Join-Path $ideaDir "fileTemplates"
$templateTarget = Join-Path $templateDir "AtCoder Library Solution.java"

if (-not (Test-Path -LiteralPath (Join-Path $librarySrc "lib"))) {
	throw "library/src/lib がありません。先に git submodule update --init --recursive を実行してください。"
}
if (-not (Test-Path -LiteralPath $modulesPath)) {
	throw ".idea/modules.xml がありません。IntelliJでAtCoderプロジェクトを一度開いてください。"
}

New-Item -ItemType Directory -Force $ideaDir, $templateDir | Out-Null

$moduleXml = @"
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://`$MODULE_DIR`$/library">
      <sourceFolder url="file://`$MODULE_DIR`$/library/src" isTestSource="false" />
      <sourceFolder url="file://`$MODULE_DIR`$/library/test" isTestSource="true" />
      <excludeFolder url="file://`$MODULE_DIR`$/library/docs" />
      <excludeFolder url="file://`$MODULE_DIR`$/library/out" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
  </component>
</module>
"@
[IO.File]::WriteAllText($modulePath, $moduleXml, [Text.UTF8Encoding]::new($false))
if (Test-Path -LiteralPath $legacyModulePath) {
	Remove-Item -LiteralPath $legacyModulePath -Force
}

function Save-Xml([xml]$Document, [string]$Path) {
	$settings = [Xml.XmlWriterSettings]::new()
	$settings.Indent = $true
	$settings.Encoding = [Text.UTF8Encoding]::new($false)
	$writer = [Xml.XmlWriter]::Create($Path, $settings)
	try {
		$Document.Save($writer)
	} finally {
		$writer.Dispose()
	}
}

[xml]$modulesDocument = Get-Content -LiteralPath $modulesPath -Raw
$modulesNode = $modulesDocument.SelectSingleNode("/project/component[@name='ProjectModuleManager']/modules")
if ($null -eq $modulesNode) {
	throw ".idea/modules.xml のProjectModuleManagerを解釈できません。"
}
$moduleNode = $modulesNode.SelectSingleNode("module[contains(@filepath, '$moduleName.iml')]")
if ($null -eq $moduleNode) {
	$moduleNode = $modulesDocument.CreateElement("module")
	[void]$modulesNode.AppendChild($moduleNode)
}
$moduleNode.SetAttribute("fileurl", "file://`$PROJECT_DIR`$/$moduleName.iml")
$moduleNode.SetAttribute("filepath", "`$PROJECT_DIR`$/$moduleName.iml")
Save-Xml $modulesDocument $modulesPath

$libraryPrefix = [IO.Path]::GetFullPath((Join-Path $root "library")) + [IO.Path]::DirectorySeparatorChar
$toolsPrefix = [IO.Path]::GetFullPath((Join-Path $root "tools")) + [IO.Path]::DirectorySeparatorChar
$configured = 0
$cleanedSourceRoots = 0

Get-ChildItem -LiteralPath $root -Recurse -Filter *.iml -File | ForEach-Object {
	$fullPath = [IO.Path]::GetFullPath($_.FullName)
	if ($fullPath -eq [IO.Path]::GetFullPath($modulePath) -or
		$fullPath.StartsWith($libraryPrefix, [StringComparison]::OrdinalIgnoreCase) -or
		$fullPath.StartsWith($toolsPrefix, [StringComparison]::OrdinalIgnoreCase)) {
		return
	}

	[xml]$document = Get-Content -LiteralPath $fullPath -Raw
	$manager = $document.SelectSingleNode("/module/component[@name='NewModuleRootManager']")
	if ($null -eq $manager) {
		return
	}

	$changed = $false
	@($manager.SelectNodes("content/sourceFolder")) | ForEach-Object {
		$url = $_.GetAttribute("url").Replace("\", "/")
		if ($url -match "/library/(src|test)$") {
			[void]$_.ParentNode.RemoveChild($_)
			$cleanedSourceRoots++
			$changed = $true
		}
	}

	if ($null -eq $manager.SelectSingleNode("orderEntry[@type='module' and @module-name='$moduleName']")) {
		$entry = $document.CreateElement("orderEntry")
		$entry.SetAttribute("type", "module")
		$entry.SetAttribute("module-name", $moduleName)
		[void]$manager.AppendChild($entry)
		$configured++
		$changed = $true
	}

	if ($changed) {
		Save-Xml $document $fullPath
	}
}

Copy-Item -LiteralPath $templateSource -Destination $templateTarget -Force

Write-Output "Library module: $modulePath"
Write-Output "Updated solution modules: $configured"
Write-Output "Removed duplicate library source roots: $cleanedSourceRoots"
Write-Output "File template: $templateTarget"
