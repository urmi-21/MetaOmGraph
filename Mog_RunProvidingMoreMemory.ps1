$minMemory = Read-Host -Prompt "Enter the minimum memory(RAM) MOG should use in GB (Ex: 1)"
$maxMemory = Read-Host -Prompt "Enter the maximum memory(RAM) MOG can use in GB (Ex: 4)"
$minMemory = $minMemory + 'g'
$maxMemory = $maxMemory + 'g'

$mogJarFile = Get-ChildItem -Path $PSScriptRoot -Filter *.jar | Select-Object -First 1

$javaRunCommand = "java -Xms" + $minMemory + " -Xmx" + $maxMemory + " -jar " + $PSScriptRoot + "\" + $mogJarFile
Invoke-Expression $javaRunCommand