#!/usr/bin/env python3
"""
ViaFabricPlus Bedrock Backport — GitHub push helper
Usage:
    python push_to_github.py <github_token> <repo_url>

Example:
    python push_to_github.py ghp_xxxx https://github.com/kullanici/viafabricplus-backport.git
"""

import subprocess
import sys
import os


def run(cmd: list[str], check: bool = True) -> subprocess.CompletedProcess:
    print(f"  $ {' '.join(cmd)}")
    result = subprocess.run(cmd, capture_output=True, text=True)
    if result.stdout.strip():
        print(result.stdout.strip())
    if result.stderr.strip():
        print(result.stderr.strip())
    if check and result.returncode != 0:
        print(f"\n[HATA] Komut basarisiz oldu: {' '.join(cmd)}")
        sys.exit(1)
    return result


def main():
    if len(sys.argv) < 3:
        print(__doc__)
        sys.exit(1)

    token = sys.argv[1].strip()
    repo_url = sys.argv[2].strip()

    # Token'i URL'ye göm (https://token@github.com/...)
    if repo_url.startswith("https://github.com/"):
        auth_url = repo_url.replace("https://", f"https://{token}@")
    elif repo_url.startswith("https://token@github.com/"):
        auth_url = repo_url
    else:
        print("[HATA] Repo URL'si 'https://github.com/...' formatinda olmali.")
        sys.exit(1)

    project_dir = os.path.dirname(os.path.abspath(__file__))
    os.chdir(project_dir)
    print(f"\n[*] Proje dizini: {project_dir}")

    # Git durumunu kontrol et
    is_git = os.path.isdir(os.path.join(project_dir, ".git"))

    if not is_git:
        print("\n[*] Git repo baslatiliyor...")
        run(["git", "init"])
        run(["git", "remote", "add", "origin", auth_url])
    else:
        print("\n[*] Mevcut git repo kullaniliyor...")
        result = run(["git", "remote", "get-url", "origin"], check=False)
        if result.returncode != 0:
            run(["git", "remote", "add", "origin", auth_url])
        else:
            run(["git", "remote", "set-url", "origin", auth_url])

    # .gitignore yoksa oluştur
    gitignore_path = os.path.join(project_dir, ".gitignore")
    if not os.path.exists(gitignore_path):
        print("\n[*] .gitignore olusturuluyor...")
        with open(gitignore_path, "w") as f:
            f.write("""# Gradle
.gradle/
build/
out/

# IDE
.idea/
*.iml
.vscode/

# OS
.DS_Store
Thumbs.db

# Secrets
push_to_github.py
""")

    # Branch adını belirle (main veya master)
    result = run(["git", "symbolic-ref", "--short", "HEAD"], check=False)
    branch = result.stdout.strip() if result.returncode == 0 else "main"
    if not branch:
        branch = "main"

    print(f"\n[*] Branch: {branch}")

    # Kullanici bilgisi
    run(["git", "config", "user.email", "build-bot@viafabricplus.local"], check=False)
    run(["git", "config", "user.name", "ViaFabricPlus Build Bot"], check=False)

    # Stage & commit
    print("\n[*] Dosyalar stage'e ekleniyor...")
    run(["git", "add", "-A"])

    result = run(["git", "status", "--porcelain"], check=False)
    if not result.stdout.strip():
        print("\n[OK] Yeni degisiklik yok, push atlandi.")
        sys.exit(0)

    run(["git", "commit", "-m", "fix: remove faulty block mixins, add GitHub Actions CI"])

    # Push
    print(f"\n[*] GitHub'a push ediliyor: {repo_url}")
    run(["git", "push", "-u", "origin", f"HEAD:{branch}", "--force"])

    print(f"\n[OK] Basariyla push edildi! Repo: {repo_url}")
    print(f"     GitHub Actions otomatik olarak JAR'i derleyecek.")
    print(f"     Ilerlemeyi buradan takip edin: {repo_url.replace('.git', '')}/actions")


if __name__ == "__main__":
    main()
