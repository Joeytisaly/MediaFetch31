Microsoft Windows [版本 10.0.19045.6456]
(c) Microsoft Corporation。保留所有权利。

C:\Users\TCPG>d:

D:\>cd D:\test

D:\test>git clone
fatal: You must specify a repository to clone.

usage: git clone [<options>] [--] <repo> [<dir>]

    -v, --[no-]verbose    be more verbose
    -q, --[no-]quiet      be more quiet
    --[no-]progress       force progress reporting
    --[no-]reject-shallow don't clone shallow repository
    -n, --no-checkout     don't create a checkout
    --checkout            opposite of --no-checkout
    --[no-]bare           create a bare repository
    --[no-]mirror         create a mirror repository (implies --bare)
    -l, --[no-]local      to clone from a local repository
    --no-hardlinks        don't use local hardlinks, always copy
    --hardlinks           opposite of --no-hardlinks
    -s, --[no-]shared     setup as shared repository
    --[no-]recurse-submodules[=<pathspec>]
                          initialize submodules in the clone
    --[no-]recursive ...  alias of --recurse-submodules
    -j, --[no-]jobs <n>   number of submodules cloned in parallel
    --[no-]template <template-directory>
                          directory from which templates will be used
    --[no-]reference <repo>
                          reference repository
    --[no-]reference-if-able <repo>
                          reference repository
    --[no-]dissociate     use --reference only while cloning
    -o, --[no-]origin <name>
                          use <name> instead of 'origin' to track upstream
    -b, --[no-]branch <branch>
                          checkout <branch> instead of the remote's HEAD
    --[no-]revision <rev> clone single revision <rev> and check out
    -u, --[no-]upload-pack <path>
                          path to git-upload-pack on the remote
    --[no-]depth <depth>  create a shallow clone of that depth
    --[no-]shallow-since <time>
                          create a shallow clone since a specific time
    --[no-]shallow-exclude <ref>
                          deepen history of shallow clone, excluding ref
    --[no-]single-branch  clone only one branch, HEAD or --branch
    --[no-]tags           clone tags, and make later fetches not to follow them
    --[no-]shallow-submodules
                          any cloned submodules will be shallow
    --[no-]separate-git-dir <gitdir>
                          separate git dir from working tree
    --[no-]ref-format <format>
                          specify the reference format to use
    -c, --[no-]config <key=value>
                          set config inside the new repository
    --[no-]server-option <server-specific>
                          option to transmit
    -4, --ipv4            use IPv4 addresses only
    -6, --ipv6            use IPv6 addresses only
    --[no-]filter <args>  object filtering
    --[no-]also-filter-submodules
                          apply partial clone filters to submodules
    --[no-]remote-submodules
                          any cloned submodules will use their remote-tracking branch
    --[no-]sparse         initialize sparse-checkout file to include only files at root
    --[no-]bundle-uri <uri>
                          a URI for downloading bundles before fetching from origin remote


D:\test>git clone https://github.com/Joeytisaly/MediaFetch31.git
Cloning into 'MediaFetch31'...
remote: Enumerating objects: 68, done.
remote: Counting objects: 100% (68/68), done.
remote: Compressing objects: 100% (59/59), done.
remote: Total 68 (delta 4), reused 2 (delta 0), pack-reused 0 (from 0)
Receiving objects: 100% (68/68), 2.32 MiB | 2.70 MiB/s, done.
Resolving deltas: 100% (4/4), done.
Encountered 12 files that should have been pointers, but weren't:
        src/imports/image-1.png
        src/imports/image-10.png
        src/imports/image-11.png
        src/imports/image-2.png
        src/imports/image-3.png
        src/imports/image-4.png
        src/imports/image-5.png
        src/imports/image-6.png
        src/imports/image-7.png
        src/imports/image-8.png
        src/imports/image-9.png
        src/imports/image.png

D:\test>cd D:\test\MediaFetch31

D:\test\MediaFetch31>git status
On branch main
Your branch is up to date with 'origin/main'.

Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git restore <file>..." to discard changes in working directory)
        modified:   src/imports/image-1.png
        modified:   src/imports/image-10.png
        modified:   src/imports/image-11.png
        modified:   src/imports/image-2.png
        modified:   src/imports/image-3.png
        modified:   src/imports/image-4.png
        modified:   src/imports/image-5.png
        modified:   src/imports/image-6.png
        modified:   src/imports/image-7.png
        modified:   src/imports/image-8.png
        modified:   src/imports/image-9.png
        modified:   src/imports/image.png

no changes added to commit (use "git add" and/or "git commit -a")

D:\test\MediaFetch31>dir
 驱动器 D 中的卷是 新加卷
 卷的序列号是 30C4-D5A4

 D:\test\MediaFetch31 的目录

2026/07/31  05:05    <DIR>          .
2026/07/31  05:05    <DIR>          ..
2026/07/31  05:05    <DIR>          .figma
2026/07/31  05:05             7,874 .gitattributes
2026/07/31  05:05               150 .gitignore
2026/07/31  05:05                40 .mise.toml
2026/07/31  05:05            10,568 AGENTS.md
2026/07/31  05:05                12 CLAUDE.md
2026/07/31  05:05    <DIR>          docs
2026/07/31  05:05    <DIR>          guidelines
2026/07/31  05:05               456 index.html
2026/07/31  05:05               627 package.json
2026/07/31  05:05            30,351 pnpm-lock.yaml
2026/07/31  05:05    <DIR>          src
2026/07/31  05:05               579 tsconfig.json
2026/07/31  05:05            12,010 vite.config.ts
              10 个文件         62,667 字节
               6 个目录 29,266,755,584 可用字节

D:\test\MediaFetch31>