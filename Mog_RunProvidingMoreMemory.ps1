$minMemory = Read-Host -Prompt "Enter the minimum memory(RAM) MOG should use in GB (Ex: 1)"
$maxMemory = Read-Host -Prompt "Enter the maximum memory(RAM) MOG can use in GB (Ex: 4)"
$minMemory = $minMemory + 'g'
$maxMemory = $maxMemory + 'g'

$mogJarFile = Get-ChildItem -Path $PSScriptRoot -Filter *.jar | Select Name

$jarPath = Join-Path -Path $PSScriptRoot -ChildPath $mogJarFile.name

$javaRunCommand = "java -Xms" + $minMemory + " -Xmx" + $maxMemory + " -jar " + $jarPath
Invoke-Expression $javaRunCommand