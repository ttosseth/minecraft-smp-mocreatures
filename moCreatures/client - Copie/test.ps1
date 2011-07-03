param
(
    [bool]$configure = $true
)

############################################################################
#######################        temporaire       ############################
############################################################################

cd D:\Minecraft\Workspaces\moCreatures\client

# configuration
if ($configure)
{
    . .\configuration\setup.ps1 "core"
}

logInfo "Updating core..."
[datetime]$startTime = date
"--"

[datetime]$endTime = date
logInfo ("Done in " + (New-TimeSpan -Start $startTime -End $endTime))
