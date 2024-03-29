# -*- coding: utf-8 -*-
"""
Created on Sat Apr  9 13:51:48 2011

@author: ProfMobius & Searge
@version: v0.1
"""

import sys, shutil, os, glob, logging, time, stat
from commands import Commands
if sys.version_info[0] == 3:
    raw_input=input
def main(conffile, force=False):

    if sys.version_info[0] == 3:
        print ('ERROR : Python3 is not supported yet.')
        sys.exit(1)

    commands = Commands(conffile)
    commands.checkupdates()

    commands.logger.info ('> Cleaning temp')
    if not reallyrmtree(commands.dirtemp):
        commands.logger.error ('failed cleaning temp')
#    os.mkdir(commands.dirtemp)

    commands.logger.info ('> Cleaning src')
    if not reallyrmtree(commands.dirsrc):
        commands.logger.error ('failed cleaning src')
#    os.mkdir(commands.dirsrc)

    commands.logger.info ('> Cleaning bin')
    if not reallyrmtree(commands.dirbin):
        commands.logger.error ('failed cleaning bin')
#    os.mkdir(commands.dirbin)

    commands.logger.info ('> Cleaning reobf')
    if not reallyrmtree(commands.dirreobf):
        commands.logger.error ('failed cleaning reobf')

    commands.logger.info ('> Cleaning jars')
    reallyrmtree(os.path.join(commands.dirjars, 'saves'))
    reallyrmtree(os.path.join(commands.dirjars, 'texturepacks'))
    reallyrmtree(os.path.join(commands.dirjars, 'world'))
    if os.path.exists(os.path.join(commands.dirjars, 'server.log')):
        os.remove(os.path.join(commands.dirjars, 'server.log'))
    for txt_file in glob.glob(os.path.join(commands.dirjars, '*.txt')):
        os.remove(txt_file)

    commands.logger.info ('> Cleaning logs')
    logging.shutdown()
    reallyrmtree(commands.dirlogs)
#    os.mkdir(commands.dirlogs)


def reallyrmtree(dir):
    i = 0
    try:
        while (os.stat(dir) and i < 20):
            shutil.rmtree(dir, onerror=onerror)
            i = i + 1
    except:
        pass

    return (i != 20)

def onerror(func, path, exc_info):
    if not os.access(path, os.W_OK):
        os.chmod(path, stat.S_IWUSR)

    time.sleep(0.5)

    try:
        func(path)
    except:
        pass


if __name__ == '__main__':
    if len(sys.argv) < 2:
        print("Syntax: python cleanup.py <configfile>")
        sys.exit(0)
    if len(sys.argv) == 3 and sys.argv[2] == '-f':
        main(sys.argv[1], True)
    else:
        main(sys.argv[1])
