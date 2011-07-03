. .\configuration\setup.ps1 "update"
    
logWarning "Updating all the mod..."
[datetime]$globalStartTime = date

.\arrange.ps1 $true
.\core.ps1 $true
.\debug.ps1 $true
.\release.ps1 $true

[datetime]$globalEndTime = date
logWarning ("Done in " + (New-TimeSpan -Start $globalStartTime -End $globalEndTime))

sleep 5