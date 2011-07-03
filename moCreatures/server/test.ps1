param
(
    [bool]$configure = $true
)

############################################################################
#######################        temporaire       ############################
############################################################################

cd D:\Minecraft\Workspaces\moCreatures\server

# configuration
if ($configure)
{
    . .\configuration\setup.ps1 "core"
}

logInfo "Updating core..."
[datetime]$startTime = date
#updateCsvFiles

[datetime]$endTime = date
logInfo ("Done in " + (New-TimeSpan -Start $startTime -End $endTime))
