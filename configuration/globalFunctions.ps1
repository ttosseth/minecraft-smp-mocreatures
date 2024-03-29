[string]$disabled = ".disabled"
[string]$backup = ".backup"

# met en pause
function pause
{
    cmd /c pause
}

# apelle le script cleanup de MCP
function cleanUp
{
    _callMcpFunction("cleanup")
}


# décompile
function decompile([string]$side = $null)
{
    if ($side -ne $null)
    {
        logInfo "Decompiling Minecraft $side..."
    }
    
    # désactivation de la partie opposée
    if ($side -eq "Client")
    {
        disable $directories.mcp.JAR.Server
    }
    elseif ($side -eq "Server")
    {
        disable $directories.mcp.JAR.Client
    }
    
    # décompilation
    cleanup
    _callMcpFunction("decompile")
    foreach ($_file in (get-childitem $directories.mcp.DEFAULT.DirSrc -Include "*~" -Recurse))
    {
        delete $_file.fullname
    }
    
    # réactivation de la partie opposée
    if ($side -eq "Client")
    {
        enable $directories.mcp.JAR.Server
    }
    elseif ($side -eq "Server")
    {
        enable $directories.mcp.JAR.Client
    }
}

# recompile
function recompile([string]$side = $null)
{
    if ($side -ne $null)
    {
        logInfo "Recompiling Minecraft $side..."
    }
    
    # désactivation de la partie opposée
    if ($side -eq "Client")
    {
        disable $directories.mcp.JAR.Server
    }
    elseif ($side -eq "Server")
    {
        disable $directories.mcp.JAR.Client
    }
    
    # recompilation
    _callMcpFunction("recompile")
    
    # réactivation de la partie opposée
    if ($side -eq "Client")
    {
        enable $directories.mcp.JAR.Server
    }
    elseif ($side -eq "Server")
    {
        enable $directories.mcp.JAR.Client
    }
}

# apelle le script reobfuscate de MCP
function reobfuscate
{
    _callMcpFunction("reobfuscate")
}

# appel de la commande 7zip
function zip([string]$params, [bool]$verbose = $false)
{
    _call "$env:ProgramFiles\7-Zip\7z.exe" $params $verbose
}

# appel de la commande jar
function jar([string]$params, [bool]$verbose = $false)
{
     _call "jar" $params $verbose
}

# permet de signer un package
function jarSign([string]$tmpJarFile, [string]$finalJarFile, [bool]$verbose = $false)
{
    _call "jarsigner" ("-keystore " + $files.privateKey + " -keypass oui8?non0? -storepass oui8?non0? -signedjar $tmpJarFile $finalJarFile Finnithnel") $verbose
}

# permet de compiler un workspace
function build([string]$workspace, [bool]$verbose = $true)
{
    _call "$env:ProgramFiles\Eclipse\eclipsec.exe" "-noSplash -data $workspace -application org.eclipse.jdt.apt.core.aptBuild" $verbose
}

# permet de créer un executable windows
function launch4jc([string]$params, [bool]$verbose = $false)
{
    _call ((Get-Item "Env:ProgramFiles(x86)").Value + "\Launch4j\launch4jc") $params $verbose
}

# supprime un fichier ou répertoire
function delete([string]$path)
{
    if (Test-Path $path)
    {
        if ((gi $path).PSIsContainer)
        {
            del $path -Recurse
        }
        else
        {
            del $path
        }
    }
}

# désactive un fichier ou répertoire
function disable([string]$item, [bool]$force = $false)
{
    _switchActivation $item ($item + $disabled) $force
}

# active un fichier ou répertoire
function enable([string]$item, [bool]$force = $false)
{
    _switchActivation ($item + $disabled) $item $force
}

# crée une sauvegarde d'un fichier ou répertoire
function backup([string]$item, [bool]$force = $false)
{
    if (Test-Path $item)
    {
        $backupItem = $item + $backup
        if ((Test-Path $backupItem) -and $force)
        {
            delete $backupItem
        }
        copy $item $backupItem -Recurse
    }
}

# restaure une sauvegarde de fichier ou répertoire
function restore([string]$item, [bool]$force = $true)
{
    _switchActivation ($item + $backup) $item $force
}

# remplace un fichier ou répertoire
function replace([string]$replaced, [string]$replacement, [bool]$force = $true)
{
    if (Test-Path $replacement)
    {
        if (!(Test-Path $replaced))
        {
            $_parentPath = split-path $replaced -Parent
            if (!(Test-Path $_parentPath))
            {
                mkdir $_parentPath | Out-Null
            }
        }
        elseIf ($force)
        {
            delete $replaced
        }
        copy $replacement $replaced -Recurse
    }
}

# fusionne deux répertoires ou fichiers
function merge([string]$toMerge, [string]$merged)
{
    if (Test-Path $toMerge)
    {
        if (!(Test-Path $merged))
        {
            replace $merged $toMerge
        }
        else
        {
            if ((gi $merged).PSIsContainer)
            {
                copy ($toMerge + "\*") $merged -Force -Recurse
            }
            else
            {
                Add-Content $merged (Get-Content $toMerge)
            }
        }
    }
}

function importMcpConf ([string]$mcpConfPath, [string]$mcpPath)
{
	[hashtable]$mcpConf = @{}
	if (Test-Path -Path $mcpConfPath)
	{
		switch -regex -file $mcpConfPath
		{
			"^\[(.+)\]$"
			{
				$category = $matches[1]
				$mcpConf.$category = @{}
			}
			"^(.+)=(.+)$"
			{
				$key = $matches[1].Trim()
				$value = $matches[2].Trim()
                if ($value -match "%\((\w+)\)s/([\w/\.]+)")
                {
                    $mcpConf.$category.$key = $value.Replace(("%(" + $matches[1] + ")s/"), ((_getIniValue $mcpConf $matches[1]) + "\")).Replace("/", "\")
                }
                elseIf ($category -ne "PACKAGES")
                {
				    $mcpConf.$category.$key = $mcpPath + "\" + $value.Replace("/", "\")
                }
                else
                {
				    $mcpConf.$category.$key = $value.Replace("/", "\")
                }
			}
		}
	}
	else
	{
		Write-Host "File not found - $mcpConfPath"
	}
	$mcpConf
}

# log une information
function logInfo([string]$message)
{
    _log ("> " + $message) White
}

# log un avertissement
function logWarning([string]$message)
{
    _log ("== " + $message + "==") Yellow
}

function updateFile([String]$file, [String]$pattern, [String]$replace)
{
    $_file = if (Test-Path $file -PathType Leaf) {
        Resolve-Path $file
    } else {
        throw "File $file not found."
    }
    $_pattern = [RegEx]::Escape($pattern)
    Invoke-Expression "`${$_file} = `${$_file} -replace '$_pattern','$replace'"
}

# effectue une copie avec exclusion
function copyExcluding([string]$source, [string]$destination, [string]$exclude)
{
    # copie récursive en utilisant la chaîne d'exclusion
    get-childitem $source -exclude $exclude -recurse | foreach ($_) {
        copy $_.fullname $_.fullname.replace($source, $destination)
    }
    
    # suppression des répertoires vides après la copie
    get-childitem $destination -recurse | Sort -Descending FullName | where { ($_.PSIsContainer) } | foreach ($_) {
        if (!(get-childitem $_.fullname))
        {
            del $_.fullname
        }
    }
}

# restaure MCP
function restoreMcp([string]$side)
{
    logInfo "Restoring MCP initial state..."

    # restauration des packages vanilla
    restore $directories.mcp.JAR.$side

    # restauration du patch vanilla
    restore $directories.mcp.PATCHES.("Patch" + $side)

    # restauration des fichiers CSV vanilla
    foreach ($_CSV in $directories.mcp.CSV.Keys)
    {
        restore $directories.mcp.CSV.$_CSV
    }
    # restauration de l'obfuscation vanilla
    restore $directories.mcp.REOBF.("ObfSRG" + $side)
    
    # nettoyage des résidus
    cleanup
}

##################################################################################################
#######################################      privates      #######################################
##################################################################################################

function _call([string]$command, [string]$params, [bool]$verbose)
{
    if ($verbose)
    {
        cmd /c ("`"`"$command`"`" $params") | Out-Default
    }
    else
    {
        cmd /c ("`"`"$command`"`" $params") | Out-Null
    }
}

function _getIniValue([hashtable]$iniHashTable, [string]$iniKey)
{
    [string]$value = $null
    foreach ($_categorie in $iniHashTable.Keys)
    {
        if ($iniHashTable.$_categorie.ContainsKey($iniKey))
        {
            $value = $iniHashTable.$_categorie.$iniKey
            break
        } 
    }
    $value
}

# appelle un script MCP
function _callMcpFunction([string]$cmdName) {
    pushd $directories.mcp.directory
    0 | cmd /c $cmdName
    popd
}

# active ou désactive un fichier ou répertoire
function _switchActivation([string]$before, [string]$after, [bool]$force)
{ 
    if (Test-Path $before)
    {
        if ((Test-Path $after) -AND $force)
        {
            delete $after
        }
        move $before $after
    }
}

# log un message
function _log([string]$message, [System.ConsoleColor]$foreColor)
{
    Write-Host -BackgroundColor Black -ForegroundColor $foreColor $message
}