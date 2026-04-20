import re

with open("user_stories.md", "r") as f:
    content = f.read()

content = content.replace("### Story 5.1 — Detect Accurate Stream, C2, and cache support\n- [ ] **Done**", "### Story 5.1 — Detect Accurate Stream, C2, and cache support\n- [x] **Done**")

with open("user_stories.md", "w") as f:
    f.write(content)
print("Marked done")
